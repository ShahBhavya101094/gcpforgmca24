To develop a web application using **Spring Boot** that allows users to view a clock based on their **Google account** preferences and **time zone**, we need to implement several key features. This includes integrating Google authentication (OAuth2), handling user preferences, determining time zones, and dynamically adjusting the view for each user based on their location and preferences.

### Key Components:
1. **Google OAuth2 Integration**: To authenticate users via their Google accounts.
2. **User Preferences**: To store user-specific settings like preferred clock style, format (12-hour/24-hour), etc.
3. **Time Zone Handling**: Detect and adjust the displayed time according to the user's time zone.
4. **Custom Views**: Display a customized clock based on the user's preferences.

### Steps to Implement:

---

### 1. **Set Up Spring Boot Project**

Create a Spring Boot project with the following dependencies:
- **Spring Web**
- **Spring Security**
- **Spring OAuth2 Client**
- **Thymeleaf** (for the UI)
- **Spring Data JPA** (to store user preferences)
- **H2/MySQL/PostgreSQL** (for the database)

#### Maven Dependencies (`pom.xml`):
```xml
<dependencies>
    <!-- Spring Boot and Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Security with OAuth2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>

    <!-- Thymeleaf -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 Database (or use MySQL/PostgreSQL) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
</dependencies>
```

---

### 2. **Configure OAuth2 for Google Login**

In `application.properties`, configure Google OAuth2 login.

```properties
# Spring Security OAuth2 settings
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
spring.security.oauth2.client.registration.google.client-name=Google

# Thymeleaf View Resolver
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

You need to replace `YOUR_GOOGLE_CLIENT_ID` and `YOUR_GOOGLE_CLIENT_SECRET` with the actual credentials from your **Google Cloud Console**.

---

### 3. **Create User Entity and Repository**

Create a `User` entity to store user preferences like time zone and preferred clock format.

#### `User.java`
```java
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    private String email; // Google account email as unique ID
    private String name;
    private String timeZone; // Store user's time zone
    private String preferredClockFormat; // 12-hour or 24-hour

    // Getters and Setters
    // ...
}
```

#### `UserRepository.java`
```java
package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
```

---

### 4. **Configure Spring Security**

Configure **Spring Security** to handle OAuth2 login.

#### `SecurityConfig.java`
```java
package com.example.demo.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true);
    }
}
```

This will redirect users to the Google login page and, upon successful login, redirect them to the `/dashboard` endpoint.

---

### 5. **Create Service to Manage User Preferences**

Create a service to fetch and update user preferences.

#### `UserService.java`
```java
package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findById(email);
    }

    // Save or update user preferences
    public void saveOrUpdateUser(User user) {
        userRepository.save(user);
    }

    // Get user info from OAuth2 authentication
    public User getUserFromAuth(Authentication authentication) {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        return userRepository.findById(email)
                .orElse(new User(email, name, "UTC", "24-hour"));
    }
}
```

---

### 6. **Create Controller to Handle Requests**

The controller will display the clock and allow users to set preferences.

#### `UserController.java`
```java
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Get user details and time zone
        User user = userService.getUserFromAuth(authentication);
        model.addAttribute("user", user);

        // Add current time based on user's time zone
        String currentTime = getCurrentTimeInTimeZone(user.getTimeZone());
        model.addAttribute("currentTime", currentTime);

        return "dashboard";
    }

    @PostMapping("/set-preferences")
    public String setPreferences(@RequestParam String timeZone, 
                                 @RequestParam String preferredClockFormat,
                                 Authentication authentication) {
        // Update user preferences
        User user = userService.getUserFromAuth(authentication);
        user.setTimeZone(timeZone);
        user.setPreferredClockFormat(preferredClockFormat);
        userService.saveOrUpdateUser(user);

        return "redirect:/dashboard";
    }

    private String getCurrentTimeInTimeZone(String timeZone) {
        // Fetch the current time based on the user's time zone
        return java.time.ZonedDateTime.now(java.time.ZoneId.of(timeZone))
                                      .toLocalTime()
                                      .toString();
    }
}
```

---

### 7. **Create Thymeleaf Views**

Create a **dashboard** view using **Thymeleaf** to display the clock and preferences.

#### `dashboard.html`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>User Dashboard</title>
</head>
<body>
    <h1>Welcome, <span th:text="${user.name}"></span>!</h1>

    <h2>Current Time (Time Zone: <span th:text="${user.timeZone}"></span>):</h2>
    <p th:text="${currentTime}"></p>

    <h3>Update Preferences</h3>
    <form action="/set-preferences" method="post">
        <label for="timeZone">Time Zone:</label>
        <input type="text" id="timeZone" name="timeZone" value="UTC" th:value="${user.timeZone}" required><br><br>

        <label for="preferredClockFormat">Preferred Clock Format:</label>
        <select id="preferredClockFormat" name="preferredClockFormat" th:value="${user.preferredClockFormat}">
            <option value="24-hour">24-hour</option>
            <option value="12-hour">12-hour</option>
        </select><br><br>

        <input type="submit" value="Update">
    </form>
</body>
</html>
```

---

### 8. **Run the Application**

Start the application by running the following command:

```bash
mvn spring-boot:run
```

---

### Conclusion

This Spring Boot application integrates **Google OAuth2** for login, manages user preferences such as time zone and clock format, and displays a dynamic clock based on those preferences. The preferences are stored using **JPA**, and the clock is displayed using **Thymeleaf** templates.

Users can log in with their Google accounts, update their preferences, and see their customized clock view based on their time zone and format choice.
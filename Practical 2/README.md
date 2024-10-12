To develop a simple Spring Boot web application that displays the current date and time in a formatted way, we can use the **Spring Web** and **Thymeleaf** templating engine to render the view. The application will fetch the current date and time from the server and display it on the webpage.

Here’s a step-by-step guide to create this application:

---

### 1. **Set Up Spring Boot Project**

You can create a Spring Boot project using **Spring Initializr** or any IDE like **IntelliJ IDEA** or **Eclipse**. You will need the following dependencies:

#### Maven Dependencies (`pom.xml`)
```xml
<dependencies>
    <!-- Spring Boot Web Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Thymeleaf Template Engine -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <!-- Spring Boot DevTools (Optional) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

### 2. **Create a Controller to Fetch Current Date and Time**

We will create a **controller** that handles the request and returns the current date and time to the view.

#### `DateTimeController.java`
```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DateTimeController {

    @GetMapping("/currentDateTime")
    public String getCurrentDateTime(Model model) {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm a");
        String formattedDateTime = now.format(formatter);

        // Add the formatted date and time to the model
        model.addAttribute("currentDateTime", formattedDateTime);

        // Return the name of the Thymeleaf template
        return "dateTime";
    }
}
```

In this controller:
- We are using `LocalDateTime.now()` to get the current date and time.
- `DateTimeFormatter.ofPattern()` is used to format the date and time in a readable way (e.g., `Monday, 01 October 2024, 03:45 PM`).
- The formatted date and time are passed to the Thymeleaf template using the `Model` object.

---

### 3. **Create a Thymeleaf Template to Display the Date and Time**

Create a Thymeleaf template that will render the formatted date and time. This template will be placed in the `src/main/resources/templates/` directory.

#### `dateTime.html`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Current Date and Time</title>
</head>
<body>
    <h1>Current Date and Time</h1>
    <p>The current date and time is:</p>
    <h2 th:text="${currentDateTime}"></h2>
</body>
</html>
```

This template will:
- Display the current date and time passed from the controller using Thymeleaf’s `${currentDateTime}`.

---

### 4. **Configure `application.properties`**

Make sure the **Thymeleaf** configuration is enabled in the `src/main/resources/application.properties` file.

```properties
# Thymeleaf properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

This configuration tells Spring Boot where to look for the Thymeleaf templates.

---

### 5. **Run the Application**

To run the application, execute the following command from the project directory:

```bash
mvn spring-boot:run
```

---

### 6. **Access the Application**

Once the application is running, you can open a browser and go to the following URL to view the current date and time:

```
http://localhost:8080/currentDateTime
```

You should see a page displaying the formatted date and time in a user-friendly format.

---

### 7. **Optional: Customizing the Date and Time Format**

You can customize the format of the date and time using `DateTimeFormatter`. Some common patterns:

- `"yyyy-MM-dd HH:mm:ss"` → `2024-10-01 15:45:30`
- `"dd/MM/yyyy hh:mm a"` → `01/10/2024 03:45 PM`
- `"EEEE, MMMM dd, yyyy"` → `Monday, October 01, 2024`
- `"MMMM dd, yyyy hh:mm a"` → `October 01, 2024 03:45 PM`

Feel free to change the format in the `DateTimeController` to meet your requirements.

---

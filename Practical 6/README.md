To demonstrate **object persistence** using **JPA** with a **Book** entity, I'll guide you through a Spring Boot application that saves, fetches, and deletes book records from a database.

### 1. **Create a Spring Boot Project**

First, create a Spring Boot project with the necessary dependencies. Use **Spring Initializr** to generate the project:

- Go to [Spring Initializr](https://start.spring.io/).
- Select:
  - **Project**: Maven Project
  - **Language**: Java
  - **Spring Boot Version**: 3.x (or higher)
  - **Dependencies**: Spring Web, Spring Data JPA, MySQL Driver (or H2 for in-memory testing)

Click on **Generate** to download the project and extract the zip file.

### 2. **Add Dependencies in `pom.xml`**

Make sure the `pom.xml` includes the necessary dependencies for Spring Boot, JPA, and MySQL (or another DB driver).

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- H2 In-Memory Database (optional for testing) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 3. **Configure Database in `application.properties`**

In `src/main/resources/application.properties`, configure the connection to the database (either MySQL or H2).

For **MySQL**:
```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/booksdb
spring.datasource.username=root
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
```

For **H2 in-memory**:
```properties
# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:booksdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
```

### 4. **Create the Book Entity**

Create a simple `Book` entity class to represent books in the database.

```java
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Double price;

    // Constructors
    public Book() {}

    public Book(String title, String author, Double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
```

### 5. **Create a Repository Interface**

The repository interface will provide the basic CRUD operations.

```java
package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

### 6. **Create a Service Layer**

The service layer will handle the business logic for saving, fetching, and deleting book objects.

```java
package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Save a new book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Fetch all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Fetch a book by ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // Delete a book by ID
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}
```

### 7. **Create a Controller**

The controller will manage the API endpoints for the book entity.

```java
package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // Save a book
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // Fetch all books
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // Fetch a book by ID
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    // Delete a book by ID
    @DeleteMapping("/{id}")
    public String deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "Book with ID " + id + " has been deleted!";
    }
}
```

### 8. **Test the Application**

You can test the application using tools like **Postman** or **cURL**.

1. **Save a new book** (POST request):
   ```bash
   POST /books
   Content-Type: application/json

   {
     "title": "Spring Boot in Action",
     "author": "Craig Walls",
     "price": 29.99
   }
   ```

2. **Fetch all books** (GET request):
   ```bash
   GET /books
   ```

3. **Fetch a book by ID** (GET request):
   ```bash
   GET /books/{id}
   ```

4. **Delete a book by ID** (DELETE request):
   ```bash
   DELETE /books/{id}
   ```

### 9. **Run the Application**

Run the Spring Boot application using the command:

```bash
mvn spring-boot:run
```

If you are using H2 in-memory database, you can view the H2 console at `http://localhost:8080/h2-console` by enabling it in `application.properties`.

### 10. **Sample Output**

1. **Save a Book:**
   ```json
   {
     "id": 1,
     "title": "Spring Boot in Action",
     "author": "Craig Walls",
     "price": 29.99
   }
   ```

2. **Fetch All Books:**
   ```json
   [
     {
       "id": 1,
       "title": "Spring Boot in Action",
       "author": "Craig Walls",
       "price": 29.99
     }
   ]
   ```

3. **Delete a Book:**
   ```text
   Book with ID 1 has been deleted!
   ```

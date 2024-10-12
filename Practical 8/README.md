Let's enhance the example to include **JPQL (Java Persistence Query Language)** queries in addition to the standard CRUD operations provided by Spring Data JPA. We'll add a few methods to demonstrate JPQL queries for operations such as finding books by title or author, and getting books within a specific price range.

### JPQL Queries Overview

**JPQL** is similar to SQL but operates on **entity objects** instead of database tables. The syntax for JPQL is mostly the same as SQL, but instead of selecting columns from tables, you select fields from entity classes.

### 1. **Enhance the `BookRepository` Interface**

You can define custom queries in the `BookRepository` using the `@Query` annotation and JPQL.

```java
package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // JPQL query to find books by author
    @Query("SELECT b FROM Book b WHERE b.author = :author")
    List<Book> findBooksByAuthor(@Param("author") String author);

    // JPQL query to find books by title
    @Query("SELECT b FROM Book b WHERE b.title = :title")
    Book findBookByTitle(@Param("title") String title);

    // JPQL query to find books within a price range
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findBooksInPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}
```

### 2. **Update the Service Layer**

The service layer will call these JPQL-based methods to perform specific queries.

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

    // JPQL - Fetch books by author
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findBooksByAuthor(author);
    }

    // JPQL - Fetch a book by title
    public Book getBookByTitle(String title) {
        return bookRepository.findBookByTitle(title);
    }

    // JPQL - Fetch books within a price range
    public List<Book> getBooksInPriceRange(Double minPrice, Double maxPrice) {
        return bookRepository.findBooksInPriceRange(minPrice, maxPrice);
    }
}
```

### 3. **Update the Controller**

Add new API endpoints in the controller to demonstrate fetching books using JPQL queries.

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

    // Fetch books by author (JPQL)
    @GetMapping("/author/{author}")
    public List<Book> getBooksByAuthor(@PathVariable String author) {
        return bookService.getBooksByAuthor(author);
    }

    // Fetch a book by title (JPQL)
    @GetMapping("/title/{title}")
    public Book getBookByTitle(@PathVariable String title) {
        return bookService.getBookByTitle(title);
    }

    // Fetch books in a price range (JPQL)
    @GetMapping("/price")
    public List<Book> getBooksInPriceRange(@RequestParam("min") Double minPrice, @RequestParam("max") Double maxPrice) {
        return bookService.getBooksInPriceRange(minPrice, maxPrice);
    }
}
```

### 4. **Run the Application**

Run the application by executing the following command:

```bash
mvn spring-boot:run
```

### 5. **Test JPQL Queries**

You can test the JPQL queries using **Postman** or **cURL**.

#### 5.1. **Add Some Books (POST Request)**

```bash
POST /books
Content-Type: application/json

{
  "title": "Spring Boot in Action",
  "author": "Craig Walls",
  "price": 29.99
}

POST /books
Content-Type: application/json

{
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "price": 39.99
}

POST /books
Content-Type: application/json

{
  "title": "Java Concurrency in Practice",
  "author": "Brian Goetz",
  "price": 45.00
}
```

#### 5.2. **Fetch Books by Author (GET Request)**

```bash
GET /books/author/Craig Walls
```

**Response**:
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

#### 5.3. **Fetch a Book by Title (GET Request)**

```bash
GET /books/title/Effective Java
```

**Response**:
```json
{
  "id": 2,
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "price": 39.99
}
```

#### 5.4. **Fetch Books in a Price Range (GET Request)**

```bash
GET /books/price?min=30&max=50
```

**Response**:
```json
[
  {
    "id": 2,
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "price": 39.99
  },
  {
    "id": 3,
    "title": "Java Concurrency in Practice",
    "author": "Brian Goetz",
    "price": 45.00
  }
]
```
package com.example.bookcatalogservice.controller;

import com.example.bookcatalogservice.model.Book;
import com.example.bookcatalogservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*") // Frontend cross-origin requests 
public class BookController {

    @Autowired
    private BookService bookService;

    // 1. Get All / Filter Books (User View)
    @GetMapping
    public List<Book> getBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return bookService.filterBooks(category, search);
    }

    // 2. Get Single Book by ID (Exception Handler through 404 Handle වේ)
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    // 3. Add New Book (Admin View)
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // 4. Update Book (Admin View)
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book bookDetails) {
        // / If the ID is missing, bookService.getBookById(id) itself will throw an exception.
        Book book = bookService.getBookById(id);

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPrice(bookDetails.getPrice());
        book.setCategory(bookDetails.getCategory());
        book.setStockQuantity(bookDetails.getStockQuantity());
        book.setImageUrl(bookDetails.getImageUrl());
        
        // The 4 newly added fields have been added right here.
        book.setLanguage(bookDetails.getLanguage());
        book.setPublisher(bookDetails.getPublisher());
        book.setIsbn13(bookDetails.getIsbn13());
        book.setDescription(bookDetails.getDescription());

        Book updatedBook = bookService.saveBook(book);
        return ResponseEntity.ok(updatedBook);
    }

    // 5. Delete Book (Admin View)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
package com.example.bookcatalogservice.service;

import com.example.bookcatalogservice.exception.BookNotFoundException;
import com.example.bookcatalogservice.model.Book;
import com.example.bookcatalogservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    //  මේක විතරයි වෙනස් කළේ: DB එකේ නැත්නම් Exception එකක් throw වෙනවා
    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    //  ඔයාගේ පරණ Code එක ඒ විදිහටමයි:
    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    //  ඔයාගේ පරණ Code එක ඒ විදිහටමයි:
    public List<Book> filterBooks(String category, String search) {
        if (category != null && !category.isEmpty()) {
            return bookRepository.findByCategoryContainingIgnoreCase(category);
        } else if (search != null && !search.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search);
        }
        return bookRepository.findAll();
    }
}
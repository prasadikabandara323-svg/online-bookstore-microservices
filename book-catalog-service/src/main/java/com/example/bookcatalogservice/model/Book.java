package com.example.bookcatalogservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {
    
    @Id
    private String id;
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private String category;
    private Integer stockQuantity;
    private String imageUrl;

    
    private String language;
    private String publisher;
    private String isbn13;
    private String description;
}
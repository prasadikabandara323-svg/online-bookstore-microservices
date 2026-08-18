package com.example.BookStore.entity;

/**
 * Defines the two roles in the system.
 * USER  -> normal customer (browses/orders books)
 * ADMIN -> manages the bookstore (add/remove books, view all orders etc.)
 */
public enum Role {
    USER,
    ADMIN
}
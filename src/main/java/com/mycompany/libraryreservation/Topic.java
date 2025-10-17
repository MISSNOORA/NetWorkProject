/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

/**
 *
 * @author gh111
 */
 import java.util.*;

public class Topic {
    private String id;
    private String name;
    private List<Book> books;

    public Topic(String id, String name, List<Book> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Book> getBooks() { return books; }

    // 🔍 يعيد الكتب المتاحة فقط
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    // 🔍 البحث عن كتاب باستخدام ID
    public Book findBookById(String bookId) {
        for (Book book : books) {
            if (book.getId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}

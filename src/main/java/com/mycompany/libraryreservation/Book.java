/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

/**
 *
 * @author gh111
 */
public class Book {
    private String id;
    private String title;
    private boolean available;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
        this.available = true; // متاح بالبداية
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }

    public void reserve() { this.available = false; }
    public void release() { this.available = true; }

    @Override
    public String toString() {
        return id + " - " + title + " (" + (available ? "Available" : "Reserved") + ")";
    }
}
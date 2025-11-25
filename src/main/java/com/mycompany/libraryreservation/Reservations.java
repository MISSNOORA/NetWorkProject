/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

/**
 *
 * @author noora
 */
public class Reservations {
    
     private String username;
    private String library;
    private String topic;
    private String book;
    private String date;

    public Reservations(String username, String library, String topic, String book, String date) {
        this.username = username;
        this.library = library;
        this.topic = topic;
        this.book = book;
        this.date = date;
    }

    public String getUsername() { 
        return username; 
    }
    public String getLibrary() {
        return library;
    }
    public String getTopic() {
        return topic;
    }
    public String getBook() {
        return book; 
    }
    public String getDate() {
        return date; 
    }
    
}

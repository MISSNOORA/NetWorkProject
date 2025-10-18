
package com.mycompany.libraryreservation;

// معلومات اليوزر


public class User {
    
    private String name;       
    private String password; 
    
    public User(int id, String name, String password) {
        this.name = name;
        this.password = password;
    }
    
    public String getName() { return name; }
    public String getPassword() { return password; }
    
    public String toString() {
        return "Name: " + name;
    }
    
    
}

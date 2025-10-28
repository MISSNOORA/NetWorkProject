
package com.mycompany.libraryreservation;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.*;

// تخزين اي بيانات في ملف
public class DataStorage {
    

    public static boolean usernameExists(String username) {
        File f = new File("users.txt");
        if (!f.exists()) return false; // no users yet

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    public static void saveUser(String name, String password) {
        try {
            FileWriter writer = new FileWriter("users.txt", true);
            writer.write(name + "|" + password + "\n");
            writer.close();
            System.out.println("User saved: " + name);
        } catch (IOException e) {
            System.out.println("Error writing to users.txt");
        }
    }

    // ---------- RESERVATIONS ----------
    public static boolean isAlreadyReserved(String library, String topic, String book, String date) {
        File f = new File("reservations.txt");
        if (!f.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                // username|library|topic|book|date
                if (p.length == 5) {
                    if (p[1].equals(library) && p[2].equals(topic) &&
                        p[3].equals(book) && p[4].equals(date)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations.txt: " + e.getMessage());
        }
        return false;
    }

    public static void saveReservation(String username, String library, String topic, String book, String date) {
        try (FileWriter fw = new FileWriter("reservations.txt", true)) {
            fw.write(username + "|" + library + "|" + topic + "|" + book + "|" + date + "\n");
        } catch (IOException e) {
            System.out.println("Error writing reservations.txt: " + e.getMessage());
        }
    }

    public static String[] getAvailableDates() {
        // change these if you want; Phase-1 demo
        return new String[] {"2025-10-16","2025-10-17","2025-10-18","2025-10-19","2025-10-20"};
    }

    // ---------- LIBRARIES / TOPICS / BOOKS ----------
    // Map: Library -> (Topic -> List<Book>)
    public static Map<String, Map<String, List<String>>> readLibraries() {
        Map<String, Map<String, List<String>>> libraries = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("libraries_composite_ids.txt"))) {
            String line;
            String currentLibrary = null;
            String currentTopic   = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Library:")) {
                    currentLibrary = line.substring(8).trim();
                    libraries.put(currentLibrary, new LinkedHashMap<String, List<String>>());
                    currentTopic = null; // reset

                } else if (line.startsWith("Topic:")) {
                    currentTopic = line.substring(6).trim();
                    if (currentLibrary != null) {
                        libraries.get(currentLibrary).put(currentTopic, new ArrayList<String>());
                    }

                } else if (line.startsWith("Book:")) {
                    String book = line.substring(5).trim();
                    if (currentLibrary != null && currentTopic != null) {
                        libraries.get(currentLibrary).get(currentTopic).add(book);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading libraries_composite_ids.txt: " + e.getMessage());
        }

        return libraries;
    }
}


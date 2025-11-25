
package com.mycompany.libraryreservation;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.*;

// تخزين اي بيانات في ملف
public class DataStorage {
    

    public static boolean usernameExists(String username) {// checking the file and whats inside it
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

    public static void saveUser(String name, String password) { //gets user info from newclient class and saves it in the file 
        try {
            FileWriter writer = new FileWriter("users.txt", true);
            writer.write(name + "|" + password + "\n");
            writer.close();
            System.out.println("User saved: " + name);
        } catch (IOException e) {
            System.out.println("Error writing to users.txt");
        }
    }
    
 
    public static List<String> getAvailableDates(String library, String topic, String book) { // dates as a starter 
        List<String> allDates = new ArrayList<>(Arrays.asList(
           "2025-10-13","2025-10-14","2025-10-15","2025-10-16", "2025-10-17", "2025-10-18", "2025-10-19", "2025-10-20", "2025-10-21","2025-10-22","2025-10-23"
        ));

        File f = new File("reservations.txt");
        if (!f.exists()) return allDates; // to display dates that has not been reserved we return every date that doesn't exist in the reservation file,by also checking library tobic and book

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 5) {
                    String fileLibrary = p[1].trim();
                    String fileTopic   = p[2].trim();
                    String fileBook    = p[3].trim();
                    String fileDate    = p[4].trim();

                    if (fileLibrary.equalsIgnoreCase(library)
                        && fileTopic.equalsIgnoreCase(topic)
                        && fileBook.equalsIgnoreCase(book)) {
                        allDates.remove(fileDate);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations: " + e.getMessage());
        }

        return allDates;
    }

    // this method is only to make sure there is no errors, and if the server displayed a reserved date
    public static boolean isAlreadyReserved(String library, String topic, String book, String date) {
        File f = new File("reservations.txt");
        if (!f.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 5) {
                    if (p.length == 5) {
                        String fileLibrary = p[1].trim();
                        String fileTopic   = p[2].trim();
                        String fileBook    = p[3].trim();
                        String fileDate    = p[4].trim();

                        if (fileLibrary.equals(library.trim()) &&
                            fileTopic.equals(topic.trim()) &&
                            fileBook.equals(book.trim()) &&
                            fileDate.equals(date.trim())) {
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations.txt: " + e.getMessage());
        }
        return false;
    }

    // save all info taken from the gui to the reservation file 
    public static void saveReservation(String username, String library, String topic, String book, String date) {
        try (FileWriter fw = new FileWriter("reservations.txt", true)) {
            fw.write(username + "|" + library + "|" + topic + "|" + book + "|" + date + "\n");
        } catch (IOException e) {
            System.out.println("Error writing reservations.txt: " + e.getMessage());
        }
    }


    // linking every library with their topics and books from the library file
    public static Map<String, Map<String, List<String>>> readLibraries() {
        Map<String, Map<String, List<String>>> libraries = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("libraries.txt"))) {
            String line;
            String currentLibrary = null;
            String currentTopic   = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Library:")) {
                    currentLibrary = line.substring(8).trim();
                    libraries.put(currentLibrary, new LinkedHashMap<String, List<String>>());
                    currentTopic = null;

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
            System.out.println("Error reading libraries.txt: " + e.getMessage());
        }

        return libraries;
    }
    public static boolean validateCredentials(String username, String password) {
    File f = new File("users.txt");
    if (!f.exists()) return false;
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 2) {
                String fileUser = parts[0].trim();
                String filePass = parts[1].trim();
                if (fileUser.equals(username) && filePass.equals(password)) {
                    return true;
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error validating credentials: " + e.getMessage());
    }
    return false;
}
    
    
    public List<Reservations> getReservationsForUser(String username) {
    List<Reservations> list = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader("reservations.txt"))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");

            if (parts.length == 5) {
                String user = parts[0];
                String library = parts[1];
                String topic = parts[2];
                String book = parts[3];
                String date = parts[4];

                if (user.equals(username)) {
                    list.add(new Reservations(user, library, topic, book, date));
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error reading reservations.txt: " + e.getMessage());
    }

    return list;
}


}


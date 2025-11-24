/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import java.util.*;

class NewClient implements Runnable{
private Socket client;
private BufferedReader in;
private PrintWriter out;
private ArrayList<NewClient> clients; // list of client connected to the server
private static final Map<String, Map<String, List<String>>> LIB_DATA = DataStorage.readLibraries(); // a method to read from a file to display info to the user

  public NewClient (Socket c,ArrayList<NewClient> clients) throws IOException
  {
    this.client = c;
    this.clients=clients;
    in= new BufferedReader (new InputStreamReader(client.getInputStream())); 
    out=new PrintWriter(client.getOutputStream(),true); // connects the input and output streams to the socket, so the server can read and send data to this spesfic client
  }
  @Override
    public void run() { // waits for any messages sent from the client and sends it to the handle message func to process 
        try {
            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            try { in.close(); out.close(); client.close(); } catch (IOException e) {}
        }
    }


    private void handleMessage(String msg) {  // we used the | to split the message and get the command needed for the server to understand 
        System.out.println("Received: " + msg);
        String[] parts = msg.split("\\|");
        
       if (msg.startsWith("REGISTER|")) { // if some how the the sent message smaller the 3 parts then there is an error ( command | username | passworde)
            if (parts.length < 3) {
                out.println("REGISTER|FAIL|Wrong format");
                return;
            }

            String name = parts[1];
            String password = parts[2];

            if (DataStorage.usernameExists(name)) { //checks part 1 which is the user name if its in the user.txt and it means its already used 
                out.println("REGISTER|FAIL|Username taken");
                System.out.println("Username already exists: " + name);
            } else {
                DataStorage.saveUser(name, password);//adds new user to the file
                out.println("REGISTER|OK");
                System.out.println("User registered successfully: " + name);
            }
            return;
        }

       if (msg.equals("GETLIBRARIES")) {
            out.println(String.join("|", LIB_DATA.keySet())); // reads librarys from the file and returns them to get displayed in the library gui
            return;
        }

        if (msg.startsWith("GETTOPICS|")) { // reads topics from the file and returns them to get displayed in the book gui
            if (parts.length < 2 || !LIB_DATA.containsKey(parts[1])) { out.println(""); return; }
            out.println(String.join("|", LIB_DATA.get(parts[1]).keySet()));
            return;
        }

        if (msg.startsWith("GETBOOKS|")) { // reads books from the file and returns them to get displayed in the book gui
            if (parts.length < 3) { out.println(""); return; }
            Map<String,List<String>> topics = LIB_DATA.get(parts[1]);
            if (topics == null) { out.println(""); return; }
            List<String> books = topics.get(parts[2]);
            if (books == null) { out.println(""); return; }
            out.println(String.join("|", books));
            return;
        }
        
        if (msg.startsWith("GETDATES|")) {// gets available dates
            String[] p = msg.split("\\|");
            if (p.length >= 4) {
                String library = p[1];
                String topic = p[2];
                String book = p[3];
                List<String> available = DataStorage.getAvailableDates(library, topic, book);
                out.println(String.join("|", available));
            } else {
                out.println("FAIL|Invalid GETDATES format");
            }
            return;
        }


        if (msg.startsWith("RESERVEBOOK|")) { // reads all given info grom the gui 
            if (parts.length < 6) { out.println("RESERVEBOOK|FAIL|format"); return; }
            String user = parts[1], lib = parts[2], topic = parts[3], book = parts[4], date = parts[5];
            
            // check if all the given info is actually right
            if (!LIB_DATA.containsKey(lib) ||
                !LIB_DATA.get(lib).containsKey(topic) ||
                !LIB_DATA.get(lib).get(topic).contains(book)) {
                out.println("RESERVEBOOK|FAIL|unknown selection");
                return;
            }

            if (DataStorage.isAlreadyReserved(lib, topic, book, date)) {// checks the reservation file if there is a reservation on the date that the client is trying to book
                out.println("RESERVEBOOK|FAIL|Already reserved on " + date); // even though we removed the reserved dates from the list but just in case
            } else {
                DataStorage.saveReservation(user, lib, topic, book, date);// saving the new reservation to the file
                out.println("RESERVEBOOK|OK|Reserved " + book + " on " + date);
            }
            return;
        }

    


    //private void outToAll(String msg) {
        //for (NewClient c : clients) {
           // c.out.println(msg);
        //}
    }
}
//test

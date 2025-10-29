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
private ArrayList<NewClient> clients;
private static final Map<String, Map<String, List<String>>> LIB_DATA = DataStorage.readLibraries();

  public NewClient (Socket c,ArrayList<NewClient> clients) throws IOException
  {
    this.client = c;
    this.clients=clients;
    in= new BufferedReader (new InputStreamReader(client.getInputStream())); 
    out=new PrintWriter(client.getOutputStream(),true); 
  }
  @Override
    public void run() {
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


    private void handleMessage(String msg) {
        System.out.println("Received: " + msg);
        String[] parts = msg.split("\\|");
       if (msg.startsWith("REGISTER|")) {
            if (parts.length < 3) {
                out.println("REGISTER|FAIL|Wrong format");
                return;
            }

            String name = parts[1];
            String password = parts[2];

            if (DataStorage.usernameExists(name)) {
                out.println("REGISTER|FAIL|Username taken");
                System.out.println("Username already exists: " + name);
            } else {
                DataStorage.saveUser(name, password);
                out.println("REGISTER|OK");
                System.out.println("User registered successfully: " + name);
            }
            return;
        }

       if (msg.equals("GETLIBRARIES")) {
            out.println(String.join("|", LIB_DATA.keySet()));
            return;
        }

        if (msg.startsWith("GETTOPICS|")) {
            if (parts.length < 2 || !LIB_DATA.containsKey(parts[1])) { out.println(""); return; }
            out.println(String.join("|", LIB_DATA.get(parts[1]).keySet()));
            return;
        }

        if (msg.startsWith("GETBOOKS|")) {
            if (parts.length < 3) { out.println(""); return; }
            Map<String,List<String>> topics = LIB_DATA.get(parts[1]);
            if (topics == null) { out.println(""); return; }
            List<String> books = topics.get(parts[2]);
            if (books == null) { out.println(""); return; }
            out.println(String.join("|", books));
            return;
        }
        
        if (msg.startsWith("GETDATES|")) {
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


        if (msg.startsWith("RESERVEBOOK|")) {
            if (parts.length < 6) { out.println("RESERVEBOOK|FAIL|format"); return; }
            String user = parts[1], lib = parts[2], topic = parts[3], book = parts[4], date = parts[5];

            // guard against typos vs file content
            if (!LIB_DATA.containsKey(lib) ||
                !LIB_DATA.get(lib).containsKey(topic) ||
                !LIB_DATA.get(lib).get(topic).contains(book)) {
                out.println("RESERVEBOOK|FAIL|unknown selection");
                return;
            }

            if (DataStorage.isAlreadyReserved(lib, topic, book, date)) {
                out.println("RESERVEBOOK|FAIL|Already reserved on " + date);
            } else {
                DataStorage.saveReservation(user, lib, topic, book, date);
                out.println("RESERVEBOOK|OK|Reserved " + book + " on " + date);
            }
            return;
        }

        out.println("UNKNOWN");
    

    //private void outToAll(String msg) {
        //for (NewClient c : clients) {
           // c.out.println(msg);
        //}
    }
}


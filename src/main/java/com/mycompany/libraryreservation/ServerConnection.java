
package com.mycompany.libraryreservation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
      
    public ServerConnection (Socket s) throws IOException{
        server=s;
        in= new BufferedReader (new InputStreamReader(server.getInputStream())); 
        out=new PrintWriter(server.getOutputStream(),true); 
    }
        @Override
    public void run(){
        
            String serverResponse;
            try {
                while(true){
                serverResponse = in.readLine(); 
                if (serverResponse==null) break;
                System.out.println("Server says: "+serverResponse);
                }
                
            } catch (IOException e) {
                 System.out.println("Connection to server is lost.");
            } finally{
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }     // handels everything thatt happens between the client and server, it reads messages and replais to the client.
    }           
}


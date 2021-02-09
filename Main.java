/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy_server;

/**
 *
 * @author user
 */import java.io.*;
import java.net.*;
public class Main {
    public static void main(String args[])
    {
        try
        {
            //First command line arguement as port number
            int n=Integer.parseInt(args[0]);
            ServerSocket serverSocket=new ServerSocket(n);
            //variable declared for performing multi-threading
            boolean isStop=false;
            while(!isStop)
            {
                //Socket is used to establish connection between proxy and client
                Socket clientSocket=serverSocket.accept();
               // System.out.println("client "+clientSocket.getInetAddress().getHostAddress()+" is connected");
               //Thread created to service the request of client
               ClientThread clientThread=new ClientThread(clientSocket);
                clientThread.start();
            }
        }
        catch(Exception e)
        {
           // System.out.println(e.toString());
        }
    }
    
}

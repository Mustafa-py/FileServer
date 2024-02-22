package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class FileServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Сервер запущен, ожидание подключения...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключен: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



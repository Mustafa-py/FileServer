package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);

                String command = consoleInput.readLine();
                out.println(command);

                if (command.equals("получить файл")) {
                    receiveFile(socket);
                } else if (command.equals("отправить файл")) {
                    sendFile(socket);
                }
            }

            in.close();
            out.close();
            consoleInput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFile(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String fileName = in.readLine();
        long fileSize = Long.parseLong(in.readLine());

        byte[] fileBytes = new byte[(int) fileSize];
        InputStream fileInputStream = socket.getInputStream();
        fileInputStream.read(fileBytes, 0, fileBytes.length);

        Files.write(Paths.get(fileName), fileBytes);
        System.out.println("Файл получен: " + fileName);

        in.close();
    }

    private static void sendFile(Socket socket) throws IOException {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.print("Введите путь к файлу для отправки: ");
        String filePath = consoleInput.readLine();
        Path path = Paths.get(filePath);

        if (Files.exists(path) && !Files.isDirectory(path)) {
            byte[] fileBytes = Files.readAllBytes(path);

            out.println(path.getFileName());
            out.println(fileBytes.length);
            socket.getOutputStream().write(fileBytes, 0, fileBytes.length);

            System.out.println("Файл отправлен: " + path.getFileName());
        } else {
            System.out.println("Файл не найден.");
        }

        consoleInput.close();
        out.close();
    }
}

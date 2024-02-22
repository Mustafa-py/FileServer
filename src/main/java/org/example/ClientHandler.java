package org.example;

import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Доступные команды: список файлов | отправить файл | получить файл | выход");

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Клиент: " + response);

                if (response.equals("список файлов")) {
                    listFiles(out);
                } else if (response.equals("отправить файл")) {
                    receiveFile(in);
                } else if (response.equals("получить файл")) {
                    sendFile(in, out);
                } else if (response.equals("выход")) {
                    break;
                }
            }

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listFiles(PrintWriter out) throws IOException {
        File folder = new File("server_files");
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                out.println(file.getName());
            }
        } else {
            out.println("Нет файлов на сервере.");
        }
    }

    private void receiveFile(BufferedReader in) throws IOException {
        String fileName = in.readLine();

        OutputStream os = new FileOutputStream("server_files/" + fileName);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read()) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
    }

    private void sendFile(BufferedReader in, PrintWriter out) throws IOException {
        String fileName = in.readLine();
        File file = new File("server_files/" + fileName);

        if (file.exists()) {
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getPath()));
            out.println(fileBytes.length);
            out.write(Arrays.toString(fileBytes), 0, fileBytes.length);
            out.flush();
        } else {
            out.println("Файл не найден на сервере.");
        }
    }
}
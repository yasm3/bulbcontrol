package com.yasm3.bulbcontrol;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class BulbCommander {

    private String host;
    private final Queue<BulbCommand> commands;

    BulbCommander(String host) {
        this.host = host;
        this.commands = new LinkedList<BulbCommand>();
    }

    public void pushCommand(BulbCommand command) {
        commands.add(command);
    }

    public void send() {
        String message = buildMessage();

        try {
            Socket socket = new Socket(host, 55443);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.print(message);
            out.flush();

            String response = in.readLine();
            System.out.println("Réponse de la lampe: " + response);

            out.close();
            in.close();
            socket.close();

            // System.out.println("message envoyé");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildMessage() {
        int position = 0;
        StringBuilder message = new StringBuilder();
        while (!commands.isEmpty()) {
            BulbCommand c = commands.poll();
            c.id = position;
            position++;

            Gson gson = new Gson();
            message.append(gson.toJson(c));
            message.append("\r\n");
        }

        System.out.println(message);
        return message.toString();
    }
}

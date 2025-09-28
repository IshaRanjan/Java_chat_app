package ChatApp;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER = "localhost";
    private static final int PORT = 8010;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Connected to chat server. Type messages:");

            // Thread for receiving messages
            new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Main thread sends user input
            String userMsg;
            while ((userMsg = userInput.readLine()) != null) {
                out.println(userMsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package ChatApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ChatClientUI extends Application {

    private TextArea chatArea;
    private TextField messageField;
    private Button sendButton;

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private static final String SERVER = "localhost";
    private static final int PORT = 8010;

    // Idle timeout
    private ScheduledExecutorService idleExecutor;
    private ScheduledFuture<?> idleTask;
    private static final int IDLE_TIMEOUT = 10; // seconds

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Chat Client");

        // --- UI Setup ---
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        messageField = new TextField();
        messageField.setPromptText("Enter message...");

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);

        HBox inputBox = new HBox(10, messageField, sendButton);
        HBox.setHgrow(messageField, Priority.ALWAYS);

        VBox root = new VBox(10, chatArea, inputBox);
        root.setPrefSize(400, 400);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();

        // --- Connect to server ---
        connectToServer();

        // --- Idle timer setup ---
        setupIdleTimer();

        // Reset idle timer when user types or sends message
        messageField.setOnKeyTyped(e -> resetIdleTimer());
        sendButton.setOnAction(e -> {
            sendMessage();
            resetIdleTimer();
        });
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER, PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                Platform.runLater(() -> chatArea.appendText("Connected to server!\n"));

                // Thread to listen for messages from server
                String messageFromServer;
                while ((messageFromServer = in.readLine()) != null) {
                    String finalMessageFromServer = messageFromServer;
                    Platform.runLater(() -> chatArea.appendText(finalMessageFromServer + "\n"));
                }

            } catch (IOException e) {
                Platform.runLater(() -> chatArea.appendText("Failed to connect to server.\n"));
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            messageField.clear();
        }
    }

    // --- Idle timeout methods ---
    private void setupIdleTimer() {
        idleExecutor = Executors.newSingleThreadScheduledExecutor();
        idleTask = idleExecutor.schedule(this::disconnectDueToIdle, IDLE_TIMEOUT, TimeUnit.SECONDS);
    }

    private void resetIdleTimer() {
        if (idleTask != null && !idleTask.isDone()) {
            idleTask.cancel(false);
        }
        idleTask = idleExecutor.schedule(this::disconnectDueToIdle, IDLE_TIMEOUT, TimeUnit.SECONDS);
    }

    private void disconnectDueToIdle() {
        Platform.runLater(() -> {
            chatArea.appendText("\nDisconnected due to 10 seconds of inactivity.\n");
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendButton.setDisable(true);
            messageField.setEditable(false);
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (socket != null && !socket.isClosed()) socket.close();
        if (idleExecutor != null) idleExecutor.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

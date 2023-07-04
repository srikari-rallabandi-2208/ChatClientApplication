package org.uynite.chat.client;

import javax.websocket.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Scanner;

@ClientEndpoint
public class ChatClient {

    private Session session;
    private String senderId;

    public ChatClient(String senderId) {
        this.senderId = senderId;
    }

    public static void startChat() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your senderId: ");
        String senderId = scanner.nextLine();

        System.out.print("Enter the WebSocket server URI (e.g., ws://localhost:8080/chat): ");
        String serverUri = "ws://localhost:8080/chat";//scanner.nextLine();

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            ChatClient chatClient = new ChatClient(senderId);
            container.connectToServer(chatClient, new URI(serverUri));

            System.out.println("Chat client started. You can start sending messages.");

            while (true) {
                System.out.print("Enter the receiverId (type 'exit' to quit): ");
                String receiverId = scanner.nextLine();

                if (receiverId.equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.print("Enter your message: ");
                String message = scanner.nextLine();

                chatClient.sendMessage(receiverId, message);
            }

        } catch (Exception e) {
            System.err.println("Failed to connect to the WebSocket server: " + e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to chat server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose() {
        System.out.println("Disconnected from chat server");
    }

    @OnError
    public void onError(Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
    }

    public void sendMessage(String receiverId, String message) {
        String jsonMessage = "{\"senderId\":\"" + senderId + "\",\"receiverId\":\""
                + receiverId + "\",\"message\":\""
                + message + ",\"timestamp\":\"" + LocalDateTime.now().toString()
                + "\"}";
        session.getAsyncRemote().sendText(jsonMessage);
    }
}


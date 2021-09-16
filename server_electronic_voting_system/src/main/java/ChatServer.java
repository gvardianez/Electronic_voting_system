
import authorization.AuthorizationService;
import authorization.InnerAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static final int PORT = 5000;
    private final AuthorizationService authService;
    private final List<ClientHandler> handlers;
    private final VotingSystem votingSystem;

    public ChatServer() {
        this.authService = new InnerAuthService();
        this.handlers = new ArrayList<>();
        this.votingSystem = new VotingSystem(authService);
    }

    public List<ClientHandler> getHandlers() {
        return handlers;
    }

    public VotingSystem getVotingSystem() {
        return votingSystem;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server start");
            while (true) {
                System.out.println("Waiting for connection");
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(0);
                System.out.println("Client connected");
                new ClientHandler(socket, this).launch();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void removeAuthorizedClientFromList(ClientHandler handler) {
        this.handlers.remove(handler);
    }

    public synchronized void addAuthorizedClientToList(ClientHandler handler) {
        this.handlers.add(handler);
    }

    public AuthorizationService getAuthService() {
        return authService;
    }



}
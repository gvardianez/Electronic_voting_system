import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final ClientController clientController;

    public NetworkService(ClientController clientController) throws IOException {
        this.clientController = clientController;
        Socket socket = new Socket(HOST, PORT);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public void readMessages() {
        Thread read = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = in.readUTF();
                    clientController.parseMessage(message);
                }
                catch (IOException e) {
                    System.out.println("break");
                    Thread.currentThread().interrupt();
                }
            }
        });
        read.start();
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
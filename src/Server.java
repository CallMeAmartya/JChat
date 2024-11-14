import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Started group chat server on port: " + this.serverSocket.getLocalPort());
        try {
            while(!this.serverSocket.isClosed()) {
                Socket socket = this.serverSocket.accept();
                System.out.println("A new client has connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServer();
        }

    }

    private void closeServer() {
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting group chat server...");
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = new ServerSocket(port);
        Server server = new Server(ss);
        server.startServer();
    }
}

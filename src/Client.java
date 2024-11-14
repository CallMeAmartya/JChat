import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final String username;
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything();
        }

    }

    private void sendMessage() {
        try {
            this.writer.write(this.username);
            this.writer.newLine();
            this.writer.flush();

            Scanner scanner = new Scanner(System.in);
            while (!this.socket.isClosed()) {
                String messageToSend = scanner.nextLine();
                this.writer.write(this.username + ": " + messageToSend);
                this.writer.newLine();
                this.writer.flush();
            }
        } catch (IOException e) {
            closeEverything();
        }

    }

    private void listenMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!socket.isClosed()) {
                        String messageFromGroup = reader.readLine();
                        System.out.println(messageFromGroup);
                    }
                } catch (IOException e) {
                    closeEverything();
                }
            }
        }).start();
    }

    private void closeEverything() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            if (this.reader != null) {
                this.reader.close();
            }
            if (this.writer != null) {
                this.writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        Socket socket = new Socket("localhost", port);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();
        Client client = new Client(socket, username);
        client.listenMessages();
        client.sendMessage();
    }
}

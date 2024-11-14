import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private static final List<ClientHandler> CLIENT_LIST = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.clientName = this.bufferedReader.readLine();
            CLIENT_LIST.add(this);
            broadcastMessage("SERVER: " + this.clientName + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(this.socket, this.bufferedReader, this.bufferedWriter);
        }
    }

    @Override
    public void run() {

        try {
            while (!this.socket.isClosed()) {
                String messageFromClient = this.bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }
        } catch (IOException e) {
            closeEverything(this.socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    private void broadcastMessage(String message) {

        for (ClientHandler clientHandler : CLIENT_LIST) {
            if (clientHandler != this) {
                try {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
                    removeClient(clientHandler);
                }
            }
        }

    }

    private void removeClient(ClientHandler clientHandler) {
        CLIENT_LIST.remove(clientHandler);
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
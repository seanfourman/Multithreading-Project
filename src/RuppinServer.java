import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class RuppinServer {
    private ArrayList<Client> clientState;

    public void startServer(int flag) {
        ServerSocket serverSocket = null;
        final int PORT = 4445;
        clientState = new ArrayList<Client>();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[SERVER] Listening on port " + PORT + "...");
            
            while (true) {
                try {
                    // accept a client connection and create a new socket for the client
                    Socket clientSocket = serverSocket.accept();

                    // create a new thread to handle the client connection
                    new ClientHandler(clientSocket, flag, clientState).start();

                } catch (IOException e) {
                    System.err.println("[SERVER] Accept failed from socket.");
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Could not listen on port: " + PORT + ".");
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("[SERVER] Server socket closed.");
                } catch (IOException e) {
                    System.err.println("[SERVER] Error closing server socket.");
                    e.printStackTrace();
                }
            }
        }
    }
}
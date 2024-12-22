import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class KnockKnockServer {
    public void startServer(int flag) {
        ServerSocket serverSocket = null;
        ArrayList<Thread> threads = new ArrayList<>();
        final int PORT = 4444;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[SERVER] Listening on port " + PORT + "...");

            while (true) {
                try {
                    // accept a client connection
                    Socket clientSocket = serverSocket.accept();

                    // create a new thread to handle the client connection
                    Thread thread = new Thread(new ClientHandler(clientSocket, flag));
                    threads.add(thread);
                    thread.start();

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
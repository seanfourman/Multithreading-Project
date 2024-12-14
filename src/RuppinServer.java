import java.net.*;
import java.io.*;

public class RuppinServer {
    public void startServer() {
        ServerSocket serverSocket = null;
        final int PORT = 4445;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[SERVER] Listening on port " + PORT + "...");

            while (true) {
                try {
                    // Accept a client connection
                    Socket clientSocket = serverSocket.accept();

                    // Create a new thread to handle the client connection
                    new ClientHandler(clientSocket).start();

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

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (
            // Create output and input streams for the client connection
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine, outputLine;

            // Instantiate the KnockKnockProtocol directly
            KnockKnockProtocol protocol = new KnockKnockProtocol();

            // Get the initial output from the protocol
            outputLine = protocol.processInput(null);
            out.println(outputLine);

            // Read input from the client and process it using the protocol
            while ((inputLine = in.readLine()) != null) {
                if ("quit".equalsIgnoreCase(inputLine)) {
                    break; // Exit if the client sends "quit"
                }
                outputLine = protocol.processInput(inputLine);
                out.println(outputLine);
                if ("Bye.".equals(outputLine)) {
                    break; // Exit if the protocol indicates the end of conversation
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the client socket
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

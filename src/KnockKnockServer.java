import java.net.*;
import java.io.*;

public class KnockKnockServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            // create a server socket listening on port 1234
            serverSocket = new ServerSocket(1234);
            System.out.println("Server is listening on port 1234...");
            
            while (true) {
                try {
                    // accept a client connection
                    Socket clientSocket = serverSocket.accept();
                    // create a new thread to handle the client connection
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.err.println("Accept failed from client socket.");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("Server socket closed.");
                } catch (IOException e) {
                    System.err.println("Error while closing the server socket.");
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

    // how each thread will handle each client connection
    public void run() {
        try {
            // create output and input streams for the client connection
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;
            // create an instance of the KnockKnockProtocol to handle the joke logic
            KnockKnockProtocol kkp = new KnockKnockProtocol();
            // get the initial output from the protocol
            outputLine = kkp.processInput(null);
            out.println(outputLine);

            // read input from the client and process it using the protocol
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("quit")) break; // exit if the client sends "quit"
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
            }

            // close the streams and the client socket
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
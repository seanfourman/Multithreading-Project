import java.net.*;
import java.io.*;

public class KnockKnockServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            // create a server socket listening on port 1234
            serverSocket = new ServerSocket(1234);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                // accept a client connection
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed from client socket.");
                System.exit(1);
            }

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

            // close the server socket
            serverSocket.close();
        }
    }
}
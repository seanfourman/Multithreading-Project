import java.io.*;
import java.net.*;

public class KnockKnockClient {
    public static void main(String[] args) throws IOException {
        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // create a socket to connect to the server at localhost on port 1234
            kkSocket = new Socket("127.0.0.1", 1234);
            // create output stream to send data to the server
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            // create input stream to receive data from the server
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: your host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: your host.");
            System.exit(1);
        }

        // create input stream to read data from the standard input (keyboard)
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        // read data from the server and send user input back to the server
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            if (fromServer.equals("Bye."))
                break;

            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println("Client: " + fromUser);
                out.println(fromUser);
            }
        }

        // close all streams and the socket
        out.close();
        in.close();
        stdIn.close();
        kkSocket.close();
    }
}
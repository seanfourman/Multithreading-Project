import java.io.*;
import java.net.*;

public class RuppinClient {
    public static void main(String[] args) throws IOException {
        Socket rpSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // create a socket to connect to the server
            rpSocket = new Socket("127.0.0.1", 4445);
            // create output stream to send data to the server
            out = new PrintWriter(rpSocket.getOutputStream(), true);
            // create input stream to receive data from the server
            in = new BufferedReader(new InputStreamReader(rpSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("[CLIENT] Unknown Host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[CLIENT] Couldn't get I/O for the connection.");
            System.exit(1);
        }

        // create input stream to read data from the standard input (keyboard)
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;

        // read data from the server and send user input back to the server
        while ((fromServer = in.readLine()) != null) {
            System.out.println("[SERVER] " + fromServer);
            if (fromServer.equals("Bye.") || fromServer.equals("Disconnecting..."))
                break;

            fromUser = stdIn.readLine();
            if (fromUser != null) {
                System.out.println("[CLIENT] " + fromUser);
                out.println(fromUser);
            }
        }

        // close all streams and the socket
        out.close();
        in.close();
        stdIn.close();
        rpSocket.close();
    }
}
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int flag; // use this flag to determine which protocol to use
    private ArrayList<Client> clientState;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.flag = 1; // default to KnockKnockProtocol
    }

    public ClientHandler(Socket socket, int flag) {
        this.clientSocket = socket;
        this.flag = flag;
    }

    public ClientHandler(Socket socket, int flag, ArrayList<Client> clientState) {
        this.clientSocket = socket;
        this.flag = flag;
        this.clientState = clientState;
    }

    @Override
    public void run() {
        try {
            // create output and input streams for the client connection
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;
            ProtocolInterface protocol; // use the ProtocolInterface to determine which protocol to use

            if (flag == 1) {
                protocol = new KnockKnockProtocol();
            } else if (flag == 2) {
                protocol = new RuppinProtocol(clientState);
            } else {
                System.err.println("[SERVER] Invalid Protocol. Please provide 1 or 2 as an argument.");
                clientSocket.close();
                return;
            }

            // get the initial output from the protocol -> start the conversation
            outputLine = protocol.processInput(null);
            out.println(outputLine);

            // read input from the client and process it using the protocol
            while ((inputLine = in.readLine()) != null) {
                if ("quit".equalsIgnoreCase(inputLine)) {
                    break; // exit if the client sends "quit"
                }
                outputLine = protocol.processInput(inputLine);
                out.println(outputLine);
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

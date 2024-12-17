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
            loadUsersFromBackup("./");
            
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

    public void loadUsersFromBackup(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("[SERVER] Invalid folder path: " + folderPath);
            return;
        }
    
        File[] backupFiles = folder.listFiles((dir, name) -> name.matches("\\d{8}_\\d{6}_backup\\.csv"));
        if (backupFiles == null || backupFiles.length == 0) {
            System.out.println("[SERVER] No backup files found in the folder.");
            return;
        }
    
        for (File file : backupFiles) {
            System.out.println("[SERVER] Reading backup file: " + file.getName());
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                br.readLine(); // skip the header line
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        String username = parts[0];
                        String password = parts[1];
                        boolean isStudent = Boolean.parseBoolean(parts[2]);
                        boolean isHappy = Boolean.parseBoolean(parts[3]);
    
                        Client client = new Client(username, password, isStudent, isHappy);
                        // this will not 100% work, it's based on the assumption that the last backup file read is the most recent (***)
                        synchronized (clientState) {
                            // remove any existing client with the same username
                            clientState.removeIf(existingClient -> existingClient.getUsername().equals(client.getUsername()));
                            // add the new client
                            clientState.add(client);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("[SERVER] Error reading file: " + file.getName());
                e.printStackTrace();
            }
        }
        System.out.println("[SERVER] Backup files loaded successfully.");
    }
}
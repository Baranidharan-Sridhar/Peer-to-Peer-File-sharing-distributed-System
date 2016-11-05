/*
 * @author  Baranidharan Sridhar
 * @version 1.8.0
 * Program Name: ClientConnection.java
 *
 */

package javap;

import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger


/* 
 * 
 * Each connected peer runs in a separate thread as a ClientConnection
 * This class is responsible for sending and receiving files across the peers
 * 
 * @param clientSelection console input to choose sending/receiving a file from the connected peers
 * @param list of chunk files present in the current peer.
 */

public class CLIENTConnection implements Runnable {

    private Socket clientSocket;
    private BufferedReader in = null;
	private ArrayList list;

    public CLIENTConnection(Socket client, ArrayList list) {
        this.clientSocket = client;
        this.list=list;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            String clientSelection;
            while ((clientSelection = in.readLine()) != null) {
                switch (clientSelection) {
                    case "1":
                        receiveFile();
                        break;
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }

                        break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
                in.close();
                break;
            }

        } catch (IOException ex) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/* 
 * 
 * If a user chooses to download a perticular file from other peers
 * This function notifies the filename to all peers
 * All peers check their list array to see if they have the file's chunk or not
 * if they have the file, they send it across through socket stream
 * 
 * 
 * 
 * @param buffer byte buffer to read file as bytes from the socket input stream
 * @param clientData datainput stream to read data from the client socket
 */
    public void receiveFile() {
        try {
            int bytesRead;

            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(("received_from_client_" + fileName));
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            clientData.close();

            System.out.println("File "+fileName+" received from client.");
        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }
/* 
 * 
 * If a user chooses to send a perticular file to other peers
 * The file divided into bytes of chunks
 * The list array of all the peers that receives the chunk will be updated
 * if they already have the chunk, the chunk will be ignored
 * 
 * 
 * 
 * @param OutputStream handle file send over socket output stream
 * 
 */
    public void sendFile(String fileName) {
        try {
            //handle file read
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }
}

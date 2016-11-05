/*
 * @author  Baranidharan Sridhar
 * @version 1.8.0
 * Program Name: Server.java
 *
 */
 
/*
 * The objective is to build a peer to peer file sharing distributed system application
 * Each peer has a client and a server and there is no centralized server.
 * A peer can request file from other active peers and all the active peers respond with a 
 * chunk if they have that particular chunk of the requested file
 * A peer can send a file which will be stored in all active peers in a distributed fashion as chunks
 *
 */
 
 package javap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/*
 * Server of a peer accepts other peers and handles them in separate thread
 * Server of a peer is also responsible for Merging back the chunks it receives from other peers
 * 
 * 
 *@param  ServerSocket server socket that runs in a unique port
 *@param  stdin  buffered reader to store command line inputs
 *@param  clientSocket each connected peer is made a clientSocket and runs in a seperate thread 
 * 
 * 
 */
public class Server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;
    private static ServerSocket serverSocket1;
    private static BufferedReader stdin;
    private static Socket clientSocket1 = null;
    static Scanner scan;
    static Scanner scan2;
    static ArrayList<String> nameList=new ArrayList<String>();
    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(44994);
            stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Server started.");
            
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
        
        try{
        
    		System.out.println("Enter file to be splitted:");
    		String fileName = stdin.readLine();
    		System.err.print("Enter number of Active peers");
    		int noOfPeers = Integer.valueOf(stdin.readLine());
            nameList=fileSplitter(fileName, noOfPeers);
    		//System.out.println("Number of chunks");
           
    	}
        
        catch (Exception e) {
            System.err.println("Error in connection attempt.");
        }
        /*
		 * Creates a separate thread for each accepted peer. 
		 *Peers request connection by pinging the server socket at its port no
		 * 
		 *
		 */
        while (true) {
            try {
            	
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);

                Thread t = new Thread(new CLIENTConnection(clientSocket,nameList));
               // Thread t1=new Thread(new CLIENTConnection(clientSocket1));

                t.start();
              //  t1.start();
            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
	/*
	 * Files are sent across the peers as chunks.
	 * The files are serialized before sending it across the networks
	 * The chunk size and no of chunks are dependent on no of active peers in the network
	 *
	 * @param List array stores the list of all chunks a peer has.
	 * @param temporary byte array that stores the data in the file as bytes.
	 * 
	 */
	private static ArrayList fileSplitter(String file, int chunk) throws IOException {
		   int totalBytesRead=0; 
		   File fileToBeRead=new File(file);
		   FileInputStream fin = new FileInputStream(fileToBeRead);
		   BufferedInputStream bin = new BufferedInputStream(fin);
		   int FILE_SIZE=(int) fileToBeRead.length();
		   int chunkSize=FILE_SIZE/chunk;
		   int NUMBER_OF_CHUNKS=0;
		   byte[] temporary=null; 
		   ArrayList<String> List=new ArrayList<String>(); 
		   while ( totalBytesRead < FILE_SIZE )
		   {
			   String PART_NAME ="data"+NUMBER_OF_CHUNKS+".bin";
			   int bytesRemaining = FILE_SIZE-totalBytesRead;
			   if ( bytesRemaining < chunkSize ) 
			   {
				   chunkSize = bytesRemaining;
				   System.out.println("CHUNK_SIZE: "+chunkSize);
			   }
			   temporary = new byte[chunkSize]; //Temporary Byte Array
			   int bytesRead = bin.read(temporary, 0, chunkSize);

			   if ( bytesRead > 0) // If bytes read is not empty
			   {
				   totalBytesRead += bytesRead;
				   NUMBER_OF_CHUNKS++;
			   }
			   File dir= new File("C:\\Users\\barae\\Desktop\\rep");
			   dir.mkdir();
			   OutputStream output = null;
			   output = new BufferedOutputStream(new FileOutputStream("C:\\Users\\barae\\Desktop\\rep\\"+PART_NAME));
			   System.out.println("Chunk Number"+NUMBER_OF_CHUNKS+" "+"C:\\Users\\barae\\Desktop\\rep\\"+PART_NAME);
			   List.add("C:\\Users\\barae\\Desktop\\rep\\"+PART_NAME);
			   output.write( temporary );
			   
		   }
		return List;
	}
}

package jp;

import java.net.*; 
import java.io.*;
public class server { 
	public static void main (String [] args ) throws IOException {
		int filesize=100022386; int bytesRead; 
		int currentTot = 0; Socket socket = new Socket("127.0.0.10",15134); 
		
		byte [] bytearray = new byte [filesize]; 
		InputStream is = socket.getInputStream(); 
		FileOutputStream fos = new FileOutputStream("C:\\Users\\barae\\Desktop\\copy1.doc"); 
		BufferedOutputStream bos = new BufferedOutputStream(fos); 
		bytesRead = is.read(bytearray,0,bytearray.length); 
		currentTot = bytesRead;
		do { bytesRead = is.read(bytearray, currentTot, (bytearray.length-currentTot));
		if(bytesRead >= 0) currentTot += bytesRead;
		System.out.println("copying");
		} 
		while(bytesRead > -1); 
		bos.write(bytearray, 0 , currentTot); 
		bos.flush(); 
		bos.close(); 
		socket.close();
		}
	} 

	
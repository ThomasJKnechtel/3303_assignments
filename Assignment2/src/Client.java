// SimpleEchoClient.java
// This class is the client side for a simple echo server based on
// UDP/IP. The client sends a character string to the echo server, then waits 
// for the server to send it back to the client.
// Last edited January 9th, 2016

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Send read/write requests to Host using UDP
 * @author Thomas
 *
 */
public class Client {
	
   DatagramPacket sendPacket, receivePacket;
   DatagramSocket sendReceiveSocket;
   
   static enum Mode{
	   netascii,
	   octet
   }
   /**
    * Initializes socket
    */
   public Client()
   {
      try {
         sendReceiveSocket = new DatagramSocket();
      } catch (SocketException se) {   // Can't create the socket.
         se.printStackTrace();
         System.exit(1);
      }
   }
   /**
    * sends request to host and waits for response
    * @param readwrite byte array 01 is read 02 is write
    * @param fName name of the file
    * @param mode the type of file: netascii or octet
    * @param hostAddress the address of the host server
    * @param hostPort the port of the Server process
    */
   public void sendAndReceive(byte[] readwrite, String fName, Mode mode, InetAddress hostAddress, int hostPort)
   {
      byte request[];
      byte msg[] = null;
      
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      
      try {
		stream.write(readwrite);
		stream.write( 0);
		stream.write(fName.getBytes());
		stream.write(0);
		stream.write(mode.toString().getBytes());
		stream.write(0);
		msg=stream.toByteArray();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      
      
      
      sendPacket = new DatagramPacket(msg, msg.length,  hostAddress, hostPort);
      
      byte packetData[] = sendPacket.getData();
      System.out.print("Sent:");
      System.out.println(Arrays.toString(packetData));
      System.out.println(DataParser.parseRequest(packetData));
      try {
          sendReceiveSocket.send(sendPacket);
       } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
       }
      byte data[] = new byte[100];
      receivePacket = new DatagramPacket(data, data.length);

      try {
         // Block until a datagram is received via sendReceiveSocket.  
         sendReceiveSocket.receive(receivePacket);
      } catch(IOException e) {
         e.printStackTrace();
         System.exit(1);
      }

      // Process the received datagram.
      System.out.println("Client: Packet received:");
      System.out.println(Arrays.toString(data));

      
      
      
   }
   /**
    * closes the Clients sockets
    */
   public void closeSocket() {
	   sendReceiveSocket.close();
   }

   public static void main(String args[])
   {  
	  
      Client c = new Client();
      try {
    	  for(int i=0; i<5; i++) {	//valid request
    	
    	 
    		  c.sendAndReceive(new byte[] {0,1}, "test.txt",Client.Mode.netascii, InetAddress.getLocalHost(), 23);
		
    		  c.sendAndReceive(new byte[] {0,2}, "test2.txt", Client.Mode.octet, InetAddress.getLocalHost(), 23);
    	  }
    	  c.sendAndReceive(new byte[] {3,2}, "invalid.txt", Client.Mode.netascii, InetAddress.getLocalHost(), 23);	//invalid request
      } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.closeSocket();
			System.exit(1);
	}
    c.closeSocket();
   }
}

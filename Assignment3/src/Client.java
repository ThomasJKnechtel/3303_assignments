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
    * sends write request to host and waits for response
    * @param readwrite byte array 01 is read 02 is write
    * @param fName name of the file
    * @param mode the type of file: netascii or octet
    * @param hostAddress the address of the host server
    * @param hostPort the port of the Server process
    */
   public void write(String fName, Mode mode, InetAddress hostAddress, int hostPort)
   {
      byte request[];
      byte msg[] = null;
      
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      
      try {
		stream.write(new byte[] {2,0});	//write request status code
		stream.write( 0);	
		stream.write(fName.getBytes());	//file name
		stream.write(0);
		stream.write(mode.toString().getBytes());	//mode
		stream.write(0);
		msg=stream.toByteArray();	//message to be sent
	} catch (IOException e1) {
		e1.printStackTrace();
		System.exit(1);
	}
      
      sendPacket = new DatagramPacket(msg, msg.length,  hostAddress, hostPort);
      
      byte packetData[] = sendPacket.getData();
      System.out.print("Sent:");
      System.out.println(Arrays.toString(packetData));
      System.out.println(DataParser.parseRequest(packetData));
      try {
          sendReceiveSocket.send(sendPacket);	//send packet to host
       } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
       }
      byte data[] = new byte[4];	//status code response
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
    	
    	 
    		  c.sendAndReceive(new byte[] {0,2}, "test.txt",Client.Mode.netascii, InetAddress.getLocalHost(), 23);	//write request
		
    		  c.sendAndReceive(new byte[] {0,1}, "test2.txt", Client.Mode.octet, InetAddress.getLocalHost(), 23);	//read request
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

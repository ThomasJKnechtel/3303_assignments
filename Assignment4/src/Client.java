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
    * @param hostPort the port of the port process
    */
   public void write(String fName, Mode mode, InetAddress hostAddress, int hostPort)
   {
      byte request[];
      byte msg[] = null;
      
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      
      try {
		stream.write(new byte[] {0,2});	//write request status code
		stream.write( 0);	
		stream.write(fName.getBytes(),3,1000);	//1000 bit file name
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

     
   }
   /**
    * sends Read request to host and waits for response
    * @param address the address of host
    * @param port the port of host
    */
   public void read(InetAddress address, int port) {
	   byte[] readRequest = new byte[] {0,1};
	   byte[] response = new byte[4];
	   
	   DatagramPacket sendPacket = new DatagramPacket(readRequest, 2, address,port );
	   try {
		sendReceiveSocket.send(sendPacket);
		receivePacket=new DatagramPacket(response,response.length);
		sendReceiveSocket.receive(receivePacket);
	   }catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	   
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
    	  for(int i=0; i<500; i++) {	//valid request
    	
    	 
    		  c.write( "test.txt",Client.Mode.netascii, InetAddress.getLocalHost(), 23);	//write request
		
    		  c.read(  InetAddress.getLocalHost(), 23);	//read request
    	  }
    	 
      } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			c.closeSocket();
			System.exit(1);
	}
    c.closeSocket();
   }
}

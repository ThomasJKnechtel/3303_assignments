import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Formatter;


public class Host {
	private DatagramSocket recieveSocket;
	private DatagramSocket sendRecieveSocket;
	private DatagramPacket receivePacket, sendPacket;
	public Host() {
		try {
			recieveSocket=new DatagramSocket(23);
			sendRecieveSocket=new DatagramSocket();
		} catch (SocketException se) {
			
			se.printStackTrace();
	        System.exit(1);
		}
	}
/**
 * Receives packet from client, sends the data to server receives response from server sends response to client.
 * @param serverAddress The address of the server
 * @param serverPort The port of the server process
 */
	 public void sendAndReceive(InetAddress serverAddress, int serverPort){
		 byte[] dataFromClient = new byte[100];
		 
		 int clientPort;
		 InetAddress clientAddress;
		 
		 
	    receivePacket = new DatagramPacket(dataFromClient, dataFromClient.length);
	    try {
	    	System.out.println("waiting");
	    	recieveSocket.receive(receivePacket);
	    }catch (IOException e) {
	    	  e.printStackTrace();
	          System.exit(1);
		}
	    //print out received packet info
	    System.out.println("Client: Packet received:");
	    System.out.print("Containing: ");
	    System.out.println(DataParser.parseRequest(dataFromClient));
	    System.out.println("Byte Array:"+Arrays.toString(dataFromClient));
	    
	    clientPort=receivePacket.getPort();
	    clientAddress=receivePacket.getAddress();
	    sendPacket=new DatagramPacket(dataFromClient,dataFromClient.length, serverAddress, serverPort); //Create packet to send to Server
		
	    try {
			sendRecieveSocket.send(sendPacket);//send packet to server
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	    byte[] fromServer = new byte[100];
	    receivePacket=new DatagramPacket(fromServer, fromServer.length);
	    try {
			sendRecieveSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	    System.out.print("Received from server:");
	    System.out.println(DataParser.parseRequest(fromServer));
	    System.out.println(Arrays.toString(fromServer));
	    sendPacket = new DatagramPacket(fromServer, fromServer.length,clientAddress,clientPort);
	    try {
			sendRecieveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	 }
	 public static void main(String[] args) {
		 Host host = new Host();
		 while(true) {
			 try {
				host.sendAndReceive(InetAddress.getLocalHost(),69);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}
		 }
	 }
}

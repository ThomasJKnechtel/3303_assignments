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
	private DatagramSocket recieveAndSendClientSocket;
	private DatagramSocket receiveAndSendServerSocket;
	
	private byte[] dataToServer, dataToClient;
	private boolean datatoServerFull, dataToClientFull;
	public Host() {
		try {
			recieveAndSendClientSocket=new DatagramSocket(23);
			receiveAndSendServerSocket=new DatagramSocket(29);
			dataToClientFull=false;
			datatoServerFull=false;
		} catch (SocketException se) {
			
			se.printStackTrace();
	        System.exit(1);
		}
	}
/**
 * Receives packet from client and sends success message back
 */
	 public void sendAndReceiveClient(){
		 
		 byte[] dataFromClient = new byte[100];
		 
		 int clientPort;
		 InetAddress clientAddress;
		 
		 
	    DatagramPacket receivePacket = new DatagramPacket(dataFromClient, dataFromClient.length);
	    try {
	    	System.out.println("waiting");
	    	recieveAndSendClientSocket.receive(receivePacket);
	    }catch (IOException e) {
	    	  e.printStackTrace();
	          System.exit(1);
		}
	    //print out received packet info
	    System.out.println("Client: Packet received:");
	    System.out.print("Containing: ");
	    System.out.println("Byte Array:"+Arrays.toString(dataFromClient));
	    
	     clientPort=receivePacket.getPort();
	    clientAddress=receivePacket.getAddress(); //the port and address the request was from.
	    if(dataFromClient[0]==0&&dataFromClient[1]==1) {	//read request from client
	    	
	    	clientReadRequest(clientAddress, clientPort);	//updates fields and sends response when there's data to be sent
	    
	    }else if(dataFromClient[0]==0&&dataFromClient[1]==1) {	//write request from client
	    	
	    	DatagramPacket response = new DatagramPacket(new byte[] {0,2,0,2} , 4,clientAddress,clientPort);	//packet contains a HTTP 0202 Request Accepted but not acted upon i.e. server hasn't handled it yet
	    	 try {
	 			recieveAndSendClientSocket.send(response);
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 			System.exit(1);
	 		}
	    	clientWriteRequest(dataFromClient);	//updates fields when previous data has been sent to server
	    
	    }
	   
	   
	 }
	 /**
	  * Request from client to read data from server. If data not empty send response to client.
	  * @param address: the address of the client request
	  * @param port: the port of the client request
	  */
	 private synchronized void clientReadRequest(InetAddress address, int port) {
		
		 while(!dataToClientFull) {	//wait for data available to be read to client
			 try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		 }
		 DatagramPacket response = new DatagramPacket(dataToClient, dataToClient.length, address, port);
		 try {
			receiveAndSendServerSocket.send(response);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		dataToClient=null;	//data has been retrieved so set to empty
		dataToClientFull=false;	//dataToClient is now empty so set to false
		notifyAll();	//notify blocked methods that lock is released
	 }
	 /**
	  * Request from client to write data to server. If previous request was sent to server, update dataToServer with new data.
	  * @param dataFromClient
	  */
	 private synchronized void clientWriteRequest( byte[] dataFromClient) {
		
		 
		 while(datatoServerFull) {	//wait for previous dataToServer to be sent to server
			 try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		 }
		 dataToServer=dataFromClient;
		 datatoServerFull=true;
		
		
	 }
	 /**
	  * Receives packet from server, sends the data to server recei
	  */
	 public void sendAndReceiveServer() {
		 byte[] dataFromServer = new byte[100];
		 DatagramPacket receivePacket = new DatagramPacket(dataFromServer, dataFromServer.length);
		 
		 try {
			receiveAndSendServerSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		 System.out.println("Server: Packet received:");
		 System.out.print("Containing: ");
		    
		System.out.println("Byte Array:"+Arrays.toString(dataFromClient));
		 
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

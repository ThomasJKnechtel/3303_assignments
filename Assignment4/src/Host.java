import java.io.ByteArrayOutputStream;
import java.lang.System;
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
	
	static boolean terminate = false;
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
		 
		 byte[] dataFromClient = new byte[1020];
		 
		 int clientPort;
		 InetAddress clientAddress;
		 
		 
	    DatagramPacket receivePacket = new DatagramPacket(dataFromClient, dataFromClient.length);
	    try {
	    	recieveAndSendClientSocket.receive(receivePacket);
	    }catch (IOException e) {
	    	  e.printStackTrace();
	          System.exit(1);
		}
	    
	     clientPort=receivePacket.getPort();
	    clientAddress=receivePacket.getAddress(); //the port and address the request was from.
	    if(dataFromClient[0]==0&&dataFromClient[1]==1) {	//read request from client
	    	clientReadRequest(clientAddress, clientPort);	//updates fields and sends response when there's data to be sent
	    
	    }else if(dataFromClient[0]==0&&dataFromClient[1]==2) {	//write request from client
	    	DatagramPacket response = new DatagramPacket(new byte[] {0,2,0,2} , 4,clientAddress,clientPort);	//packet contains a HTTP 0202 Request Accepted but not acted upon i.e. server hasn't handled it yet
	    	 try {
	 			recieveAndSendClientSocket.send(response);
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 			System.exit(1);
	 		}
	    	clientWriteRequest(dataFromClient);	//updates fields when previous data has been sent to server
	    
	    }else if(dataFromClient[0]==0&&dataFromClient[1]==3) {	//termination request
	    	clientWriteRequest(dataFromClient);
	    	terminate=true;
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
		
		notifyAll();
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
		InetAddress serverAddress = receivePacket.getAddress();
		int serverPort = receivePacket.getPort();
		
		if(dataFromServer[0]==0&&dataFromServer[1]==1) {	//read request from server
		    	
		    readRequestServer(serverAddress, serverPort);//updates fields and sends response when there's data to be sent
		    
		 }else{	//write request from server
		    	
		    	DatagramPacket response = new DatagramPacket(new byte[] {0,2,0,2} , 4,serverAddress,serverPort);	//packet contains a HTTP 0202 Request Accepted but not acted upon i.e. client hasn't read it yet
		    	 try {
		 			recieveAndSendClientSocket.send(response);
		 		} catch (IOException e) {
		 			e.printStackTrace();
		 			System.exit(1);
		 		}
		    	writeRequestServer(dataFromServer);//updates fields when previous data has been sent to server
		    
		    }
	 }
	 /**
	  * Waits for dataToServer to be full before sending data back to server and updating fields
	  * @param address the address of server
	  * @param port the port of server
	  */
	 private synchronized void readRequestServer(InetAddress address, int port) {
		 
		 while(!datatoServerFull) {	//wait for there to be data in dataToServer to send
			 try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		 }
		 DatagramPacket response = new DatagramPacket(dataToServer, dataToServer.length, address, port);
		 try {
			receiveAndSendServerSocket.send(response);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		dataToServer=null;
		datatoServerFull=false;	//data was sent so no longer full. Notify blocked methods that lock is released.
		notifyAll();
	 }
	 /**
	  * When data can be written to dataToClient, update fields for it. 
	  * @param dataFromServer the data from server
	  */
	 private synchronized void writeRequestServer(byte[] dataFromServer) {
		 while(dataToClientFull) {	//wait for previous data to be sent to client to send
			 try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		 }
		 dataToClient=dataFromServer;
		 dataToClientFull=true;
		 notifyAll();
	 }
	 public static void main(String[] args) {
		 Host host = new Host();
		 Thread monitorClient = new Thread(new Runnable() {	//monitor client socket for requests and handle them
			
			@Override
			public void run() {
				long startTime = System.nanoTime();
				while(!terminate) {
					
					host.sendAndReceiveClient();
				}
				
				
			}
		});
		 Thread monitorServer = new Thread(new Runnable() {	//monitor server socket for requests and handle them
			
			@Override
			public void run() {
				while(!terminate) {
					host.sendAndReceiveServer();
				}
				
				
			}
		});
		
		monitorClient.start();
		monitorServer.start();	//begin monitoring server and client
		 
	 }
}

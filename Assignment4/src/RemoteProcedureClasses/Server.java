package RemoteProcedureClasses;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Server implements Runnable{

	private DatagramSocket sendAndReceiveSocket;
	
	private byte[] hostData = new byte[1020];
	private byte sendData[]=null;
	
	private boolean terminate= false;
	public Server() {
		
		try {
			sendAndReceiveSocket=new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * receives packet from host check if request is valid 
	 * @throws Exception Throws exception if the request is invalid
	 */
	public void read() throws Exception{
		
		byte[] readRequest = new byte[] {0,1};
		boolean isRead=false;
		boolean isWrite=false;
		
		DatagramPacket sendPacket=null;
		DatagramPacket responsePacket = null;
		
		try {
			sendPacket = new DatagramPacket(readRequest, 2, InetAddress.getLocalHost(),29);
			sendAndReceiveSocket.send(sendPacket);
			responsePacket = new DatagramPacket(hostData, hostData.length);
			sendAndReceiveSocket.receive(responsePacket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	
		if(hostData[0]==0 && hostData[1]==1) {	//read request
			isRead=true; 
			isWrite=false;
			sendData= new byte[]{0,3,0,1};
		}
		else if(hostData[0]==0 && hostData[1]==2) {	//write request
			isRead=true;
			isWrite=false;
			sendData=new byte[] {0,4,0,0};
		}
		int zeroCount=0;
		boolean prevByteZero = false;
		
		if(!isRead&&!isWrite) {
			terminate=true;
			//throw( new Exception("Invalid Input: not a read or write request"));
			
		}
		
		
		
	}
	/**
	 * create write request for server, send it and wait for response
	 * @throws Exception if Invalid input
	 */
	public void write(){
		
		
		DatagramPacket send, response;
		byte[] hostResponse = new byte[4];
		response=null;
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), 29);
			sendAndReceiveSocket.send(send);
			response=new DatagramPacket(hostResponse, hostResponse.length);
			sendAndReceiveSocket.receive(response);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
				
	}

	@Override
	public void run() {
		while(true) {	//send read and write requests to host
			try {
				read();
			} catch (Exception e) {
				System.exit(0);
				e.printStackTrace();
			}
			if(terminate)break;
			write();
		}
		
	}
	
}

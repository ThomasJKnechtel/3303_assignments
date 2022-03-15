import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Server implements Runnable{

	private DatagramSocket sendAndReceiveSocket;
	
	private byte[] hostData = new byte[100];
	private boolean isRead=false;
	private boolean isWrite=false;
	private byte sendData[]=null;
	
	public Server() {
		
		try {
			sendAndReceiveSocket=new DatagramSocket(29);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * receives packet from host check if request is valid and send response to host
	 * @throws Exception Throws exception if the request is invalid
	 */
	public void read() throws Exception{
		
		byte[] readRequest = new byte[] {0,1};
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
		System.out.println(hostData);
		
		
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
		
		for(int i=2; i<hostData.length; i++) {
			if(hostData[i]==0) {
				if(prevByteZero&&zeroCount<2) { // two zeros in a row and not enough arguments
					throw(new Exception("Invalid Input: not enough arguments"));
					
				}
				zeroCount++;
				prevByteZero=true;
			}
			else if(prevByteZero&&zeroCount>2){ // if not zero and is fourth argument
				throw(new Exception("Invalid Input: too many arguments"));
			}
			else {
				prevByteZero=false; //if not zero and valid
			}
			
		}
		if(zeroCount<2) {
			throw(new Exception("Invalid Input: not enough arguments"));
		}else if(!isRead&&!isWrite) throw( new Exception("Invalid Input: not a read or write request"));
		
		
		
	}
	/**
	 * create write request for server, send it and wait for response
	 * @throws Exception if Invalid input
	 */
	public void write(){
		
		
		DatagramPacket send, response;
		try {
			send = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), 29);
			sendAndReceiveSocket.send(send);
			response=new DatagramPacket(hostData,hostData.length);
			sendAndReceiveSocket.receive(response);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println(Arrays.toString(hostData));
		
	}
	public static void main(String[] args) {
		Server server = new Server();
		Thread monitorHost = new Thread(server);
		monitorHost.start();
		
	}
	@Override
	public void run() {
		while(true) {	//send read and write requests to host
			try {
				read();
			} catch (Exception e) {
				System.exit(1);
				e.printStackTrace();
			}
			write();
		}
		
	}
	
}

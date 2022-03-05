import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class Server {
	private DatagramPacket send, receive;
	private DatagramSocket sendSocket, receiveSocket;
	public Server() {
		
		try {
			sendSocket=new DatagramSocket();
			receiveSocket=new DatagramSocket(69);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * receives packet from host check if request is valid and send response to host
	 * @throws Exception Throws exception if the request is invalid
	 */
	public void sendRecieve() throws Exception {
		
		byte[] hostData = new byte[100];
		boolean isRead=false;
		boolean isWrite=false;
		byte sendData[]=null;
		
		receive= new DatagramPacket(hostData, hostData.length);
		
		try {
			receiveSocket.receive(receive);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.print("Recieved: ");
		System.out.println(DataParser.parseRequest(hostData));
		System.out.println(Arrays.toString(hostData));
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
		
		send = new DatagramPacket(sendData, sendData.length, receive.getAddress(), receive.getPort());
		System.out.print("Sent: ");
		System.out.println(Arrays.toString(send.getData()));
		sendSocket.send(send);
		
		
	}
	public static void main(String[] args) {
		Server server = new Server();
		while(true) {
			try {
				server.sendRecieve();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
}

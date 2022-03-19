package RemoteProcedureClasses;

public class RemoteProcedureSystem {
	public static void main(String[] args) {
		 
		Server server = new Server();
		Host host = new Host();
		Client client = new Client();
		
		Thread thread1 = new Thread(server);
		thread1.start();
		host.monitorClient();
		host.monitorServer();
		client.sendRequests();
		
	
	}
}

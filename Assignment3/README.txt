Set Up:
	1. load the project into IDE.
	2. Run Server.java as Java Application
	3. Run Host.java as Java Application
	4. Run Client.java as Java Application
Classes:
	Client.java
		Sends 10 read or write request and 1 invalid request that contains a file name and the mode of file ie. netascii or octet to the host. It then waits for a response and prints the response.
	Host.java
		Waits for a request from a Client process. The request is then sent to the server and waits for a response from the server. The Host then sends the response to the Client process that initated the request.
	Server.java
		Waits for a request. The request is then validated to check that it is either read/write and that only file name and mode are in the request. Sends a 0301 Response for a read request or a 0400 for a write request.
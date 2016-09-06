package catalystgo.com.cg.cat.ucc;

import java.util.Scanner;

public class InputToServerThread {
	protected boolean server; 
	protected boolean updating;
	protected String IP; 
	protected String port;

	public InputToServerThread(boolean server, boolean updating, String IP, String port) {
		this.server = server;
		this.updating = updating;
		this.IP = IP;
		this.port = port;
		
		Thread inputThread = new Thread(new Runnable() {
			public void run() {

				Scanner scan = new Scanner(System.in);
				String message = "";
				while (true) {
					System.out.println("Ready for input: ");
					message = scan.nextLine();
					System.out.println("Sending message: " + message);
					try {
						connectAndPost(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		});
		new Thread(inputThread).start();
	}
	
	public void connectAndPost(String message) {
		Connections connections = new Connections(server, updating, IP, port, message);
	}

}

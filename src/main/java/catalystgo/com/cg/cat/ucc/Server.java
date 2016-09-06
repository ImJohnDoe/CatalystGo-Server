package catalystgo.com.cg.cat.ucc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final String IP = null;
	private static final String port = Integer.toString(8080);
	static ServerSocket serverSocket = null;
	static Socket clientSocket = null;
	static boolean listening = true;

	public static void main(String[] args) throws IOException {
	    try {
	        serverSocket = new ServerSocket(8080);
	    } catch (IOException e) {
	        ServerThread.showmsg("Could not use port: 8080");
	        System.exit(-1);
	    }
	    
	    ServerThread.showmsg("server- initialized");
	    ServerThread.showmsg("server- waiting...");

	    InputToServerThread inputToServerThread = new InputToServerThread(false, true, IP, port);
	    while (listening)
	        new ServerThread(serverSocket.accept()).start();
	}
}

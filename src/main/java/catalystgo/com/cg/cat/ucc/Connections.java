package catalystgo.com.cg.cat.ucc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

import javax.print.DocFlavor.BYTE_ARRAY;

public class Connections {
	String IP, port;
	String message = "";
	Socket socket;

	public Connections(boolean server, boolean updating, String IP, String port, String message) {
		this.IP = IP;
		this.port = port;
		try {
			socket = new Socket(IP, Integer.parseInt(port));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (!server) {
			if (updating) {
				try {
					sendVersion();
					updating();
					read();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Hit Client line");
			}
		}
		if (server) {
			System.out.println("Hit Server line");
		}
	}

	public void sendVersion() throws IOException, FileNotFoundException {

		File file = getResourceFile("version.txt");
		FileReader fileReader = null;
		fileReader = new FileReader(file);
		System.out.println("Version File open to send");
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String stringRead = bufferedReader.readLine();
		System.out.println("Printing String read: " + stringRead);
		bufferedReader.close();

		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
	    byte[] data = stringRead.getBytes();
	    dos.writeInt(data.length);
		dos.write(data);
		dos.flush();
	}

	public void updating() throws IOException, FileNotFoundException {
		int filesize = 1024;
		
		int bytesRead;
		int current = 0;
		
		System.out.println("Hit Updating code");
		showmsg("client- connected");

	    byte[] byteArray = new byte[filesize];
		java.io.InputStream inStream = socket.getInputStream();
		bytesRead = inStream.read(byteArray, 0, byteArray.length);
		current = bytesRead;

		do {
			bytesRead = inStream.read(byteArray, current, (byteArray.length - current));
			if (bytesRead >= 0)
				current += bytesRead;
		} while (bytesRead > 0);

		RedisAccess redisAccess = new RedisAccess();
		redisAccess.add("recieved.txt", new String(byteArray));
		System.out.println("Printing ByteArray: " + new String(byteArray));
		inStream.close();
		socket.close();
	}

	public void read() throws IOException, FileNotFoundException {
		RedisAccess redisAccess = new RedisAccess();
		redisAccess.get("recieved.txt");
	}

	public File getResourceFile(String fileName) {
		File file = null;
		URL res = getClass().getResource("/" + fileName);
		if (res == null || res.toString().startsWith("jar:")) {
			try {
				InputStream input = getClass().getResourceAsStream("/resources/" + fileName);
				file = File.createTempFile("tempfile", ".tmp");
				OutputStream out = new FileOutputStream(file);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				file.deleteOnExit();
			} catch (IOException ex) {
				ServerThread.showmsg("IO Exception while loading " + fileName + " exception: " + ex);
				System.exit(-1);
			}
		} else {
			// this will probably work in your IDE, but not from a JAR
			file = new File(res.getFile());
		}
		if (file != null && !file.exists()) {
			throw new RuntimeException("Error: File " + file + " not found!");
		}
		return file;
	}

	public static void showmsg(String s) {
		System.out.println(s);
		// JOptionPane.showMessageDialog(null, s);
	}
}

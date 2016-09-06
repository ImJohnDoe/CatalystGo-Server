package catalystgo.com.cg.cat.ucc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

public class ServerThread extends Thread {
Socket socket;
ObjectInputStream in;
ObjectOutputStream out;
String version = "0.0.1";

public ServerThread(Socket socket) {
    super("Server Thread");
    this.socket = socket;
}

public void run() {
    showmsg("server- Accepted connection : " + socket);
    getVersion();
    sendFile();
}

public void getVersion() {
	showmsg("entered into getVersion()");
    try {
        DataInputStream ois = new DataInputStream(
                socket.getInputStream());
        System.out.println("printing ois " + ois.toString());
        try {
            int len = ois.readInt();
            System.out.println("printing len " + len);
            byte[] data = new byte[len];
            ois.read(data, 0, len);
            System.out.println("printing data " + data);
            
            String s = new String(data);
            System.out.println("printing s " + s);
            
            if (s.equals(version)) {
                System.out.println("server- matched version :)");
            } else {
                System.out.println("server- didnt match version :(");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void sendFile() {
    // sendfile
	File myFile = getResourceFile("test_data.csv");
    byte[] mybytearray = new byte[(int) myFile.length()];
    FileInputStream fis;
    try {
        fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray, 0, mybytearray.length);
        OutputStream os = socket.getOutputStream();
        showmsg("server- Sending...");
        os.write(mybytearray, 0, mybytearray.length);
        os.flush();
        socket.close();
        System.out.println("File written to socket");
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
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
    //JOptionPane.showMessageDialog(null, s);
}
}


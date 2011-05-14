
import java.net.*;
import java.io.*;

public class Connection extends Thread {
	private Socket con;
	private Server server;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	

	public Connection(Socket con, Server server) {
		this.con = con;
		this.server = server;
		
		try {
			in = new ObjectInputStream(con.getInputStream());
			out = new ObjectOutputStream(con.getOutputStream());
		} catch (Exception e) {
			reportError("IO:"+e.getMessage());
		}
	}

	private void reportError(String s) {
		System.out.println("Error: " + s);
	}

	public synchronized void send(String s) throws IOException {
		out.writeChars(s);
		out.flush();
	}

	public String read() throws IOException {
		return in.readUTF();
	}

	public String getHostName() {
		return con.getInetAddress().getHostName();
	}

	public void run() {
		System.out.println("Connection received from "
				+ con.getInetAddress().getHostName());
		try {
			server.addConnection(this);
			while (true) {
				String s = read();
				System.out.println(s);
				
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				close();
				System.out.println("closeing connection with "
						+ con.getInetAddress().getHostName() + " ...");
			} catch (Exception e1) {
				System.out.println("closeing connection with "
						+ con.getInetAddress().getHostName() + " ...");
				return;
			}
		}
	}


	public void close() throws Exception {
		con.close();
		server.removeConnection(this);
		System.out.println(con.getInetAddress().getHostName() + " is disconecting...");
	}
}
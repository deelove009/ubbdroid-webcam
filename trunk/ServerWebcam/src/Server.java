
import java.net.*;
import java.util.*;

public class Server {
	private boolean isStarted = false;
	private boolean isRuning = false;
	private List<Connection> connections;

	private ServerSocket ss;
	private int port= 4567;

	public Server(int port) {
		this.port = port;
		connections = new LinkedList<Connection>();
	}

	public void open() throws Exception {
		if (!isStarted) {
				ss = new ServerSocket(port);
				System.out.println("Starting  Server...");
		}
		isStarted = true;
	}
	
	@SuppressWarnings("deprecation")
	private void close() throws Exception{
		for(Connection c :connections){
			try{
				c.stop();
				c.close();
				connections.remove(c);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		ss.close();
		isStarted = false;
	}
	
	public void run() {
		isRuning = true;
		try {
			open();
			while (isRuning) {
				Connection connection = new Connection(ss.accept(), this); 
				connection.start();
			}
		} catch (Exception e) {
			System.out.println("Server error: " + e.getMessage());
		} finally {
			try {
				close();
				System.out.println("Server closing...");
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				System.out.println("Server canot closing...");
			}
		}
	}
	
	


	public void removeConnection(Connection c) {
		connections.remove(c);
	}
	public void addConnection(Connection c){
		connections.add(c);
	}
	
	
	public int getPort() {
		return port;
	}

	public static void main(String[] args) {
		Server server;
		try {
			try {
				server = new Server(Integer.parseInt(args[0]));
			} catch (Exception e) {
				server = new Server(4567);
			}
			server.run();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

import java.net.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.text.html.HTMLDocument.Iterator;

public class Connection extends Thread {
	private Socket con;
	private Server server;

	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private MjpegStreamServer ms;
	private DatabaseConnection coni;
	
	private String username = "";
	/**
	 * Konstruktor
	 */
	public Connection(Socket con,MjpegStreamServer ms, Server server) {
		this.con = con;
		this.server = server;
		this.ms = ms;
		coni = new DatabaseConnection(); 
		try {
			out = new ObjectOutputStream(con.getOutputStream());
			in = new ObjectInputStream(con.getInputStream());
		} catch (Exception e) {
			reportError("IO:" + e.getMessage());
		}
	}

	private void reportError(String s) {
		System.out.println("Error: " + s);
	}
/**
 * 
 */
	public synchronized void sendBytes(byte[] buffer) throws IOException {
		OutputStream os = con.getOutputStream();
		os.write(buffer);
		os.flush();
	}
/**
 * A send tovabbitja az adatokat, az android aplikacionak
 */
	public synchronized void send(Object s) throws IOException {
		out.writeObject(s);
		out.flush();
	}
/**
 * A read kiolvassa az android aplikaciotol kapott adatokat
 */
	public Object read() throws Exception {
		return in.readObject();
	}

	public String getHostName() {
		return con.getInetAddress().getHostName();
	}
/**
 * A run elindit egy szalat.
 * Beolvassa az adoot felhasznalo nevet es jelszavat, ezt leellenorzi, majd megfelelo uzenetet 
 * kuld vissza a ConnectionLayer-nek, ami feldolgozza, majd a MainActivity visszakuldi a felhasznalo 
 * nevet, ez majd bekerul az android tablaba, es egy 4-est, ha megy a streaming.
 */
	public void run() {
		System.out.println("Connection received from "
				+ con.getInetAddress().getHostName());
		try {
			// server.addConnection(this);
			int type = (Integer) read();
			switch (type) {

			case 1:

				DatabaseConnection co = new DatabaseConnection();
				String user = (String) read();
				String password = (String) read();
				

				System.out.println(user + ":" + password);
				if (co.control(user, password)) {
					co.adduser(user);
					send(new Integer(2));
					
				} else {
					send(new Integer(3));
				}
				break;

			case 4:
				String usern = (String) read();
				username  = usern;
				login(usern);
				
				while (true) {
					
					byte data[] = (byte[]) read();
					BufferedImage i = ImageIO.read(new ByteArrayInputStream(
							data));
					System.out.println(data.length);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try {
						ImageIO.write(i, "jpg", baos);
						ms.sendJPEG(baos.toByteArray(),username);
					} catch (Exception e) {
						reportError(e.getMessage());
					}
				}
			default:
				reportError("Not compatibele pakege");
				break;
			}
			con.getInputStream();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				logout(username);
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
	/**
	 * A login meghiva az adduser fuggvenyt a DatabaseConnection osztalybol,
	 * ami az android tablabol beszurja az adott felhasznalo nevet
	 */
	private void login(String user){
	    coni.adduser(user);
	}
	/**
	 * A logout meghiva az deletuser fuggvenyt a DatabaseConnection osztalybol,
	 * ami az android tablabol kitorli az adott felhasznalo nevet.
	 */
	private void logout(String user){
		coni.deleteuser(user);
	}
/**
 * Az extracted letrehozza a kapcsolatot az android aplikacioval.
 */
	private boolean extracted(DatabaseConnection con, String user,
			String password) {
		return con.control(user, password);
	}
/**
 * Az close lezarja a kapcsolatot az android aplikacioval.
 */
	public void close() throws Exception {
		con.close();
		server.removeConnection(this);
		System.out.println(con.getInetAddress().getHostName()
				+ " is disconecting...");
	}
}
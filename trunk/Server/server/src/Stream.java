import java.net.Socket;


public class Stream {
	private Socket connection;
	private String user;
/**
 * 	Stream konstruktora
 */
	public Stream(Socket connection, String user) {
		super();
		this.connection = connection;
		this.user = user;
	}
	/**
	 * Socket az android aplikacio elerese
	 */
	public Socket getConnection() {
		return connection;
	}
    /**
     * setConnection: az android aplikacioval valo akpcsolat letrehozasa
     */
	public void setConnection(Socket connection) {
		this.connection = connection;
	}
	/**
	 *getUser: felhasznalo nev lekerdezese
	 */
	public String getUser() {
		return user;
	}
	/**
	 * setUser: felhasznalo nev kuldese
	 */
	public void setUser(String user) {
		this.user = user;
	}
}

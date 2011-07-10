

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	private String DBUrl = "jdbc:mysql://localhost:3306/ubb";
	private Connection conn = null;

	/*
	 * A connection fuggveny letrehozza a kapcsolatot a Sql tablaval
	 */
	public Statement connect() throws SQLException, Exception {
		Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		conn = DriverManager.getConnection(DBUrl, "root", "lovacska");

		Statement stmt = conn.createStatement();

		return stmt;
	}
	
	/**
	 * A control leellenorzi, hogy a megadott user megtalalhato-e a
	 * tablaban, es helyes a megadott password, ennek megfeleloen egy boolean erteket terit vissza,
	 *  majd meghivja a disconnect fuggvenyt
	 **/
	public  boolean control(String user, String password) {
		boolean s = false;
		try {

			Statement keres = connect();
			keres.executeQuery("SELECT password FROM acount WHERE user='" + user
					+ "'");
			ResultSet rs = keres.getResultSet();
			if (rs.next()) {
				String passwordtabla = rs.getString("password");
				if (password.equals(passwordtabla)) {
					s = true;
				}
			}
			rs.close();
			disconnect(keres);
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return s;
	}
	
	/**
	 * A listuser egy listaba menti azokat a felhszanalokat, akik androidrol vannak bejelentkezve. 
	 * Ezt a listat visszateriti, majd meghivja a disconnect fuggvenyt.
	 **/
	
	public List<String> listuser() {
		List<String> l = new LinkedList<String>();
		try {

			Statement keres = connect();
			keres.executeQuery("SELECT user FROM android ");
			ResultSet rs = keres.getResultSet();
			while (rs.next()) {
				String usertabla = rs.getString("user");
				l.add(usertabla);
			}
			rs.close();
			disconnect(keres);
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return l;

		
	}

	/**
	 * Az adduser a parameterkent megkapott felhasznalo nevet egy uj tablaba teszi (android).
	 * Ennek a tablanak az elemei, azok a felhasznalo nevek, melyek androidrol jelentkeztek be.
	 **/
	public void adduser(String user) {
		try {

			Statement update = connect();
			int s = update
					.executeUpdate("INSERT INTO android (user) VALUES ('"
							+ user + "')");
			disconnect(update);
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Az deletuser a parameterkent megkapott felhasznalo nevet kitoroli az android tablabol.
	 * Ennek a tablanak az elemei, azok a felhasznalo nevek, melyek androidrol jelentkeztek be.
	 **/
	public void deleteuser(String user) {
		try {

			Statement delete = connect();
			boolean s = delete.execute("DELETE FROM android WHERE user='" + user
					+ "'");
			disconnect(delete);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * a register egy uj felhasznalot hoz letre a tablaban, majd meghivja a
	 * disconnect fuggvenyt
	 **/
	public boolean register(String user, String password) {
		try {

			Statement update = connect();
			int s = update
					.executeUpdate("INSERT INTO acount (user, password) VALUES ('"
							+ user + "', '" + password + "')");
			disconnect(update);
			return (s != 0);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/** A disconnect lezarja  a kapcsolatot a sql tablaval  **/
	public void disconnect(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) {
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException sqlEx) {
				System.out.println(sqlEx.getMessage());
			}
		}
	}
}

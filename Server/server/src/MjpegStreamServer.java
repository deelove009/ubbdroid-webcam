import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class MjpegStreamServer extends Thread {
	private ServerSocket ss;
	private int port;
	private List<Stream> connections;
/**
 * MjpegStreamServer konstruktor
 */
	public MjpegStreamServer(int port) {
		this.port = port;
		connections = new LinkedList<Stream>();
	}
/**
 * open: kommunikacio megnyitasa az android aplikacioval
 */
	public void open() throws Exception {
		System.out.println("Starting Streamserver...");
		ss = new ServerSocket(port);
	}
/**
 * close: kommunikacio lezarasa az android aplikacioval
 */
	public void close() throws Exception {
		System.out.println("Closeing Streamserver...");
		ss.close();
	}
/**
 *sendJPEG: megkapja az elkudendo jpgt es usert beirja a soketbe, es elkuldi a nezonek
 */
	public synchronized void sendJPEG(byte[] data, String user){
		for (Stream s : connections) {
			if (s.getUser().equals(user)) {
				try {
					OutputStream os = s.getConnection().getOutputStream();
					os.write(("--BoundaryString\r\n"
							+ "Content-type: image/jpg\r\n"
							+ "Content-Length: " + data.length + "\r\n\r\n")
							.getBytes());
					os.write(data);
					os.write("\r\n\r\n".getBytes());
					os.flush();
				} catch (Exception e) {
					System.out.println(e.getMessage());
					connections.remove(s);
				}
			}
		}
	}
/**
 *run elinit egy szerver soket-et, ha filmeznek,akkor megkapja a kepet, ennek a header-jet, 
 *majd atkonvertalja a kepet 
 */
	public void run() {
		try {
			open();
			while (true) {
				Socket connection = ss.accept();
				System.out.println("Stream reqest from: "
						+ connection.getLocalAddress().getCanonicalHostName());
				BufferedReader r = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String user = r.readLine().split(" ")[1].substring(1);

				connection.getOutputStream().write(
						("HTTP/1.0 200 OK\r\n" + "Server: MobileWebcam\r\n"
								+ "Connection: close\r\n" + "Max-Age: 0\r\n"
								+ "Expires: 0\r\n"
								+ "Cache-Control: no-cache, private\r\n"
								+ "Pragma: no-cache\r\n"
								+ "Content-Type: multipart/x-mixed-replace; "
								+ "boundary=--BoundaryString\r\n\r\n")
								.getBytes());
				connections.add(new Stream(connection, user));
			}
		} catch (Exception e) {
			try {
				close();
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}
		}
	}
}

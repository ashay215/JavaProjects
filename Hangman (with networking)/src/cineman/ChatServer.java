package cineman;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private Vector<ServerThread> serverThreads;
	public ChatServer(int port) {
		ServerSocket ss = null;
		serverThreads = new Vector<ServerThread>();
		try {
			ss = new ServerSocket(port);
			while (true) {
				System.out.println("waiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress() + ", "+ s.getPort());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
				System.out.println(serverThreads.size());
			}
		} catch (IOException ioe) {
			System.out.println("ioechatserver: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
		}
	}

	public void sendMessageToAllClients(ChatMessage message) {
		for (ServerThread st : serverThreads) {
			st.sendMessage(message);
		}
	}

	public static void main(String [] args) {
		new ChatServer(6789);
	}
}

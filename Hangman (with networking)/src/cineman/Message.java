package cineman;
import java.io.Serializable;

public class Message implements Serializable {
	public static final long serialVersionUID = 1;
	
	private String username;
	private String gamename;
	private String messagetype;
	
	public Message(String username, String gamename, String type) {
		this.username = username;
		this.gamename = gamename;
		this.messagetype = type;
	}
	
	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}

	public String getUsername() {
		return username;
	}

	public String getMessagetype() {
		return messagetype;
	}	
	

}

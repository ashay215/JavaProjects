package cineman;
import java.util.ArrayList;

public class JoinGameMessage extends Message {
	//a class to facilitate players joining the game
	private static final long serialVersionUID = 1L;

	private ArrayList<String> joinedusers;
	boolean validgame = false;
	boolean validname = false;

	public JoinGameMessage(String username, String gamename, String type) {
		super(username, gamename, type);
		joinedusers = new ArrayList<String>();
	}


	public ArrayList<String> getJoinedusers() {
		return joinedusers;
	}
	
	public void addtojoined(String toadd){
		joinedusers.add(toadd);
	}


	public boolean isValidgame() {
		return validgame;
	}


	public void setValidgame(boolean validgame) {
		this.validgame = validgame;
	}


	public boolean isValidname() {
		return validname;
	}


	public void setValidname(boolean validname) {
		this.validname = validname;
	}

}

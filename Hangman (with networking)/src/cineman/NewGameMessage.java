package cineman;
import java.util.ArrayList;

public class NewGameMessage extends Message {
	//a class to facilitate setting up the game
	private static final long serialVersionUID = 1L;
	
	private int playernum;
	private int waitingfor;	
	private ArrayList<String> joinedusers;

	public NewGameMessage(String username, String gamename, String type, int playernum) {
		super(username, gamename, type);
		this.playernum = playernum;
		this.waitingfor = playernum-1; // waiting for everyone except creator
		joinedusers = new ArrayList<String>();
		joinedusers.add(username);
	}


	public void setWaitingfor(int waitingfor) {
		this.waitingfor = waitingfor;
	}

	public int getPlayernum() {
		return playernum;
	}

	public int getWaitingfor() {
		return waitingfor;
	}


	public ArrayList<String> getJoinedusers() {
		return joinedusers;
	}
	
	public void setJoinedusers(ArrayList<String> joined) {
		joinedusers = joined;
	}

}

package cineman;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Game {

	private ArrayList<UserThread> userThreads;
	private ArrayList<String> wordbank;
	private String creator;
	private String gamename;
	private int playernum;
	private int waitingfor;
	private String gameword;
	
	public String getGamename() {
		return gamename;
	}
	
	public Game(UserThread ogthread, String creator, String gamename, int playernum, ArrayList<String> wordbank){
		
		userThreads = new ArrayList<UserThread>();
		this.wordbank = wordbank;
		this.userThreads.add(ogthread);
		this.creator = creator;
		this.gamename = gamename;
		this.playernum = playernum;
		this.waitingfor = playernum-1;						
		
		Random rand = new Random();			
		int n = rand.nextInt(wordbank.size());
		this.gameword = wordbank.get(n);
		//System.out.println("randomword: " + wordbank.get(n) );
	}

	public int getWaitingfor() {
		return waitingfor;
	}

	public void setWaitingfor(int waitingfor) {
		this.waitingfor = waitingfor;
	}

	public ArrayList<UserThread> getUserThreads() {
		return userThreads;
	}
	
	public void addPlayer(UserThread toadd){
		userThreads.add(toadd);
	}

	public int getPlayernum() {
		return playernum;
	}

	public String getGameword() {
		return gameword;
	}
}

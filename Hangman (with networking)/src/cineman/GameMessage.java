package cineman;
import java.util.ArrayList;

public class GameMessage extends Message {
	//a class to facilitate moves and keep track of states ingame
	private static final long serialVersionUID = 1L;

	ArrayList<String> usernames;//usernames of all players
	ArrayList<Integer> hangmanprogress;//the progress for each player
	boolean gameover = false;
	String gameword;
	ArrayList<Character> guessedletters;//keeps track of letters guessed so far
	String lastuser=null;
	String lastmove=null;
	ArrayList<Character> guessedword;//filled in phrase so far
	ArrayList<Character> fullword;//phrase in array form
	ArrayList<Character> wrongguesses;
	String eliminated = null;

	public GameMessage(String username, String gamename, String type, int playernum) {
		super(username, gamename, type);
		hangmanprogress = new ArrayList<Integer>(playernum);
		for(int i=0; i <playernum; i ++){
			hangmanprogress.add(new Integer(0));
		}		
		usernames = new ArrayList<String>(playernum);
		guessedletters = new ArrayList<Character>();
		guessedword = new ArrayList<Character>();
		fullword = new ArrayList<Character>();
		wrongguesses = new ArrayList<Character>();
	}
	
	public void makeword(){
		String[] splitwords = gameword.split(" ");
		
		for ( int i=0; i < splitwords.length; i++) {
			String word = splitwords[i];
			for(int j=0; j<word.length(); j++){
				fullword.add(word.charAt(j));
				fullword.add(' ');
				guessedword.add('_');
				guessedword.add(' ');
				if(j==word.length()-1 && i<splitwords.length -1){
					guessedword.add(' ');
					guessedword.add(' ');
					fullword.add(' ');
					fullword.add(' ');
				}
			}
		}
	}

}

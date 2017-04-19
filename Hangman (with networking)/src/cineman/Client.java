package cineman;
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class Client extends Thread {
	public static void main (String [] args){					
		new Client();
	}
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	Scanner scanner;
	boolean gameover=false;
	String username;
	String word=null;
	ArrayList<String> usernames=null;
	ArrayList<Character> wordsofar=null;
	ArrayList<Character> guessedletters = null;
	ArrayList<Character> fullword = null;
	ArrayList<Integer> hangmanprogress=null;
	ArrayList<Character> wrongguesses =null;
		
	private Client(){
				 
		//boolean running = true;//while the program(function, really) is running
		scanner = new Scanner(System.in);
		setup();
	}
	
	private void setup(){
		System.out.println("Welcome to Cineman!\n");					
		
		boolean connected = false;
		while(!connected){
			
			System.out.println("Please Enter the ipaddress.");
			String hostname = scanner.nextLine();	
			int port = 0;
			
				boolean validport = false;
				while (!validport){
					System.out.print("Please enter the port.\n");
					try{
						port = scanner.nextInt();		
						scanner.nextLine();
					}
					catch(InputMismatchException e){
						System.out.println("Invalid port. Please enter the port.");
						continue;
					}	
					validport = true;
				}
			//port=8000;
			
			//System.out.println(hostname + " " + port);
			
			Socket s = null;
			try {
				s = new Socket(hostname, port);

				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());				
				connected=true;
				System.out.println("Congratulations! You have connected to the Cineman server!");
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
				ioe.printStackTrace();
				System.out.println("Unable to connect to server with provided fields.");
			} /*finally {
				try {
					if (s != null) {
						s.close();
					}
					if (scanner != null) {
						scanner.close();
					}
				} catch (IOException ioe) {
					System.out.println("ioechatclient: " + ioe.getMessage());
				}
			}*/
											
		}		
		
		boolean gamechoice = false;
		while(!gamechoice){
			System.out.print("1. Start a game \n2. Join a game\n");
			try{
				int input = scanner.nextInt();	
				scanner.nextLine();
				if(input != 1 && input != 2){
					System.out.println("You have entered an invalid command, please try again.");
					continue;
				}
				else if(input ==1){
					newgame();
				}
				else if(input == 2){
					joingame();
				}	
				gamechoice=true;
			}
			catch(InputMismatchException e){
				System.out.println("You have entered an invalid command, please try again.");
				scanner.next();
				continue;
			}
		}
	}
	
	private void newgame(){
		
		String uname;
		String gamename = null;
		int playernum=0;
	
		System.out.println("Please enter your username \n");
		uname = scanner.nextLine();		
		
		boolean uniquename = false;
		while(!uniquename){
			System.out.println("Please enter a unique name for your game. \n");
			gamename = scanner.nextLine();	
	
			UniqueGameMessage message = new UniqueGameMessage(uname, gamename, "UniqueGameMessage");
			try {
				oos.writeObject(message);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				UniqueGameMessage receivedmessage = (UniqueGameMessage)ois.readObject();
				if(receivedmessage.isUnique()){
					uniquename = true;
					System.out.println("Game name " + receivedmessage.getGamename()  +" is unique, verification recieved on client!"  );
				}
				
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioechatclient: " + ioe.getMessage());
			}
			
			if(!uniquename){
				System.out.print("This name has already been chosen by another game.");
				continue;
			}
			
		}
		
		System.out.print("\nPlease enter the number of players(1-4) in this game.");
		boolean validplayernum = false;
		while(!validplayernum){
			
			try{
				playernum = scanner.nextInt();	
				scanner.nextLine();
				if(playernum < 1 || playernum > 4){
					System.out.println("Invalid number of players. Please enter the number of players (1-4) in this game.");
					continue;
				}
				
				validplayernum=true;
			}
			catch(InputMismatchException e){
				System.out.println("You have entered an invalid command, please try again.");
				continue;
			}
		}	
		
		NewGameMessage newgamemessage = new NewGameMessage(uname, gamename, "NewGameMessage", playernum);
		try {
			oos.writeObject(newgamemessage);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int waitingfor = playernum-1;
		if(waitingfor != 0) System.out.println("Waiting for " + waitingfor + " players to join...");
		
		while(waitingfor > 0){
			try {
				NewGameMessage receivedmessage = (NewGameMessage)ois.readObject();
				waitingfor = receivedmessage.getWaitingfor();
				
				ArrayList<String> joined = receivedmessage.getJoinedusers();
				if(joined.size() > 1) System.out.println(joined.get(joined.size()-1) + " joined the game.");

				if(waitingfor == 0){
					System.out.print("All players have joined: ");
					
					for(int i=0; i<joined.size();i++){
						System.out.print(joined.get(i));
						if(i<joined.size()-1) System.out.print(",");
					}
					System.out.print("\n");
				}
				else{
					System.out.println("Waiting for " + waitingfor + " players to join...");
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioechatclient: " + ioe.getMessage());
			}
		}	
		username = uname;
		startgame(uname, gamename, playernum);
		
	}//ends newgame method
	
	private void joingame(){
		
		String uname=null;
		String gamename = null;
		
		boolean validgame = false;
		while(!validgame){
			System.out.println("Please enter the name of the game you wish to join. \n");
			gamename = scanner.nextLine();	
	
			JoinGameMessage message = new JoinGameMessage(uname, gamename, "JoinGameMessage");
			try {
				oos.writeObject(message);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {//userthread processes and checks if valid game to join
				JoinGameMessage receivedmessage = (JoinGameMessage)ois.readObject();
				if(receivedmessage.isValidgame()){
					validgame = true;
					gamename = receivedmessage.getGamename();
					System.out.println("Game name " + receivedmessage.getGamename()  +" is valid, verification recieved on client!"  );
				}
				
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioechatclient: " + ioe.getMessage());
			}
			
			if(!validgame){
				System.out.println("This game does not exist or has reached the maximum number of players.");
				continue;
			}
			
		}
		
		boolean validname = false;
		while(!validname){
			System.out.println("Please enter your username.\n");
			uname = scanner.nextLine();	
	
			JoinGameMessage message = new JoinGameMessage(uname, gamename, "JoinGameMessage");
			try {
				oos.writeObject(message);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {//userthread processes and checks if valid username to join with
				JoinGameMessage receivedmessage = (JoinGameMessage)ois.readObject();
				if(receivedmessage.isValidname()){
					validname = true;
					System.out.println("User name " + receivedmessage.getUsername()  +" is valid, verification recieved on client!"  );
					username = uname;
				}
				
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioechatclient: " + ioe.getMessage());
			}
			
			if(!validname){
				System.out.println("This username has already been chosen by one of the other players.");
				continue;
			}			
		}		
		int playernum=0;
		System.out.println("Waiting for other players to join...");
		int waitingfor = 4;
		while(waitingfor > 0){
			try {
				NewGameMessage receivedmessage = (NewGameMessage)ois.readObject();
				waitingfor = receivedmessage.getWaitingfor();
				playernum = receivedmessage.getPlayernum();
				ArrayList<String> joined = receivedmessage.getJoinedusers();

				if(waitingfor == 0){
					System.out.print("All players have joined: ");
					
					for(int i=0; i<joined.size();i++){
						System.out.print(joined.get(i));
						if(i<joined.size()-1) System.out.print(",");
					}
					System.out.print("\n");
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioechatclient: " + ioe.getMessage());
			}
		}	
		username = uname;
		startgame(uname, gamename, playernum);
		
	}
	
	private void incrementhangman(){
		int i= userindex(username);
		int prevprog = hangmanprogress.get(i).intValue();
		hangmanprogress.set(i, new Integer(prevprog +1) );
	}
	
	private void startgame(String uname, String gamename, int playernum){
		
		try {//get initial game info
			GameMessage receivedmessage = (GameMessage)ois.readObject();
			gameover = receivedmessage.gameover;			
			word = receivedmessage.gameword;
			usernames = receivedmessage.usernames;
			wordsofar = receivedmessage.guessedword;
			guessedletters = receivedmessage.guessedletters;
			fullword = receivedmessage.fullword;
			hangmanprogress = receivedmessage.hangmanprogress;
			wrongguesses = receivedmessage.wrongguesses;
			
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioechatclient: " + ioe.getMessage());
		}		
		
		printUI(wordsofar, fullword, guessedletters, 0);
		
		this.start();
		while(!gameover) {//to send messages
			boolean eliminated= false;
			
			int p= userindex(username);
			int prevprog = hangmanprogress.get(p).intValue();
			if(prevprog < 7) System.out.println(username + ", please enter a character or the entire phrase as a guess." );
			
			
			String guess = scanner.nextLine();
			if(prevprog ==7){
				System.out.println("You have been eliminated. You may only spectate for the rest of the game.");
				continue;
			}			
			
			
			System.out.println("You guessed '" + guess + "'");
			if(guess.length() >1){//if guessing phrase
				if(guess.equalsIgnoreCase(word)){
					System.out.println("You guessed right! You win!");
					wordsofar = fullword;
					gameover=true;
				}
				else{
					System.out.println("You guessed wrong! :(");
					incrementhangman();
				}
			}
			else{//if guessing letter
				boolean uniqueguess = true;
				for(int i=0; i < guessedletters.size(); i++){
					if(Character.toLowerCase(guessedletters.get(i).charValue()) == Character.toLowerCase(guess.charAt(0))){
						System.out.println("This letter has already been guessed! Try again!");
						uniqueguess = false;
						break;
					}
				}
				if(!uniqueguess) continue;
				
				guessedletters.add(guess.charAt(0));
				boolean charmatch = false;
				for(int i=0; i < word.length(); i++){
					if(word.charAt(i) == guess.charAt(0)){
						charmatch=true;
					}
				}
				if(charmatch){
					System.out.println("You guessed right!");
					for(int i=0; i < fullword.size(); i++){
						if(Character.toLowerCase(guess.charAt(0)) == Character.toLowerCase(fullword.get(i).charValue())){
							wordsofar.set(i, fullword.get(i) );
						}
					}
					boolean wordcomplete = true;
					for(int i=0; i < fullword.size(); i++){
						if(fullword.get(i).charValue() != '_' && fullword.get(i).charValue() != ' ' ){
							wordcomplete = false;
						}
					}
					if(wordcomplete) gameover=true;
					System.out.println("You win!");
				}
				else{
					System.out.println("You guessed wrong! :(");
					wrongguesses.add(guess.charAt(0));
					incrementhangman();
				}
				
			}
			
			
			prevprog = hangmanprogress.get(p).intValue();
			if(prevprog == 7) eliminated = true;
			
			int completedcount = 0;
			for(int i=0; i <hangmanprogress.size(); i++){
				if(hangmanprogress.get(i) == 7) completedcount++;
			}
			if (completedcount == hangmanprogress.size() -1) gameover = true;
			
			//if(gameover) //System.out.print("");			
			
//			for(int i=0; i < guessedletters.size(); i++){
//				System.out.println(guessedletters.get(i));
//			}
			
			printUI(wordsofar, fullword, guessedletters, prevprog);
			if(prevprog == 7) System.out.println("Your hangman has been completed. You lose. :(");
		
			
			GameMessage message = new GameMessage(uname, gamename, "GameMessage", playernum);
			message.gameover = gameover;
			message.lastmove = guess;
			message.usernames = usernames;
			message.hangmanprogress = hangmanprogress;
			message.gameword = word;
			message.guessedletters = guessedletters;
			message.lastuser = username;
			message.guessedword = wordsofar;
			message.fullword = fullword;
			message.wrongguesses = wrongguesses;
			if(eliminated) message.eliminated = username;
			
			try {
				oos.writeObject(message);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			
		}
	}
	
	private void printUI(ArrayList<Character> wordsofar, ArrayList<Character> fullword, ArrayList<Character> guessedletters, int hangnum){
		System.out.println("----------------------------------------------------\n");
		System.out.println(" -----			Cineman");
		
		System.out.print(" |   ");
		if(hangnum == 7){
			System.out.println("|");
		}
		else{
			System.out.print("\n");
		}
		
		System.out.print(" |   ");
		if(hangnum >= 1){
			System.out.println("@");
		}
		else{
			System.out.print("\n");
		}
		
		System.out.print(" |  ");
		if(hangnum >= 3){
			System.out.print("/");
		}
		else{
			System.out.print(" ");
		}
		if(hangnum >=2){
			System.out.print("|");
		}
		if(hangnum >= 4){
			System.out.print("\\");
			System.out.print("\n");
		}
		else{
			System.out.print("\n");
		}
		System.out.print(" |   ");
		if(hangnum >=2){
			System.out.print("|");
		}
		else{
			System.out.print(" ");
		}
		System.out.print("		");	
		printphrase(wordsofar);
		System.out.print(" |  ");
		if(hangnum >=5){
			System.out.print("/ ");
		}
		else{
			System.out.print(" ");
		}
		if(hangnum >=6){
			System.out.print("\\");
		}
		System.out.print("\n");
		System.out.println(" |");
		System.out.println("_|_____");
	
		for(int i=0; i < wrongguesses.size(); i++ ){
			System.out.print(wrongguesses.get(i));
		}
		System.out.println("\n----------------------------------------------------");
		
	}
	
	private void printphrase(ArrayList<Character> word){
		for(int i=0; i <word.size(); i++){
			System.out.print(word.get(i));
		}
		System.out.print("\n");
	}
	
	public void run() {//to receive messages
		try {
			while(true) {
								
				GameMessage message = (GameMessage)ois.readObject();
				
				System.out.println(message.lastuser + " guessed '" + message.lastmove + "'");				
				wordsofar = message.guessedword;
				guessedletters = message.guessedletters;
				gameover = message.gameover;
				hangmanprogress = message.hangmanprogress;
				wrongguesses = message.wrongguesses;
				gameover = message.gameover;
				
//				for(int i=0; i < message.guessedletters.size(); i++){
//					System.out.println(message.guessedletters.get(i));
//				}
				
				int p2= userindex(message.getUsername());
				int prevprog2 = hangmanprogress.get(p2).intValue();				
				
				int p= userindex(username);
				int prevprog = hangmanprogress.get(p).intValue();
				
				printUI(wordsofar, fullword, guessedletters, prevprog);
				if(prevprog2 == 7) System.out.println(message.getUsername() + " has completed their hangman. They have been eliminated. :/");
				if(gameover){
					System.out.println("Game over!" + message.getUsername() + " has won!");
					System.out.println("Losers: ");
					for(int i=0; i<usernames.size();i++){
						if(!usernames.get(i).equals(message.getUsername())){
							System.out.print(usernames.get(i));
						}
						if(i<usernames.size()-1) System.out.print(",");
					}
					System.out.print("\n");
					System.exit(0);
				}
				
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioechatclient: " + ioe.getMessage());
		}
	}
	
	private int userindex(String uname){
		for(int i=0; i < usernames.size(); i++){
			if(usernames.get(i).equals(uname)) return i;
		}
		return -1;
	}
	
}





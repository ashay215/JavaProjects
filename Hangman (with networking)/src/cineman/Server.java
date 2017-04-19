package cineman;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Server {
	
	private ArrayList<String> wordbank;	
	private ArrayList<UserThread> userThreads;
	private ArrayList<Game> gamebase;
		
	public ArrayList<String> getWordbank() {
		return wordbank;
	}

	public void setGamebase(ArrayList<Game> gamebase) {
		this.gamebase = gamebase;
	}

	public static void main (String [] args){
		new Server();
	}
	
	public void addGame(UserThread userthread, String creator, String gamename, int playernum){
		gamebase.add(new Game(userthread, creator, gamename, playernum, wordbank));
	}
	
	public Server(){
		
		Scanner scanner = new Scanner(System.in);
		int port = -1;
		
		boolean validport = false;
		while (!validport){
			System.out.println("Please enter the port to host the server.");
			try{
				port = scanner.nextInt();	
				scanner.nextLine();
			}
			catch(InputMismatchException e){
				System.out.println("Invalid port.");
				continue;
			}	
			validport = true;
		}
		
		makewordbank(scanner);//populates wordbank		
		
		
		port = 8000;
		ServerSocket ss = null;
		userThreads = new ArrayList<UserThread>();
		gamebase = new ArrayList<Game>();
		try {
			ss = new ServerSocket(port);
			System.out.println("Server started!");// and listening with wordcount: " + wordbank.size());
			while (true) {
				//System.out.println("waiting for connection...");
				Socket s = ss.accept();
				//System.out.println("Connection from " + s.getInetAddress() + ", Port: "+ s.getPort());
				UserThread st = new UserThread(s, this);
				userThreads.add(st);
				//System.out.println(userThreads.size());
			}
		} 
		catch (IOException ioe) {
			System.out.println("ioechatserver: " + ioe.getMessage());
		} 
		finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
		}
	
		scanner.close();
	}
	
	public void killgame(String gamename){
		int tokill = -1;
		Game tokillg = null;
		for(int i=0; i < gamebase.size(); i++){
			if(gamebase.get(i).getGamename().equals(gamename)){
				tokill = i;
				tokillg = gamebase.get(i);
			}
		}
		ArrayList<UserThread> threads= tokillg.getUserThreads();
		for(int i=0; i < threads.size(); i++){
			for(int j=0; j <userThreads.size();j++  ){
				if(userThreads.get(j).equals(threads.get(i))){
					userThreads.remove(i);
				}
			}			
		}
		gamebase.remove(tokill);
	}
	public void makewordbank(Scanner scanner){
		System.out.println("Please enter the path to the xml file used for the game phrases.");
		String filepath = scanner.nextLine();		
		//String filepath = "test.xml";
		String errormessage = "";
		Database database = new Database(filepath);
		
		while(!database.isParsed()){
			errormessage = database.getError();
			System.out.println("Error: " + errormessage);
			System.out.println("Please try entering the xml file again!");
			filepath = scanner.next();	
			database = new Database(filepath);
		}		
		ArrayList<Movie> Moviebase = database.getMoviebase();
		wordbank = new ArrayList<String>();
		
		for(int i=0; i <Moviebase.size(); i++){//creating wordbank
			Movie currmovie = Moviebase.get(i);
			
			wordbank.add(currmovie.getTitle());
			
			ArrayList<String> currwriters = currmovie.getWriters();//for all writers
			for(int j=0; j < currwriters.size(); j++){
				wordbank.add(currwriters.get(j));
			}
			
			ArrayList<Actor> curractors = currmovie.getActors();//for all actors
			for(int j=0; j <curractors.size(); j++){
				wordbank.add(curractors.get(j).getFirstname() + " "+ curractors.get(j).getLastname());
			}			
		}
	}
	
	public void sendtoallingame(Message message) {
		String gamename = message.getGamename();
		Game togame=null;
		for(int i=0; i < gamebase.size(); i++){
			if(gamebase.get(i).getGamename().equals(gamename)){
				togame = gamebase.get(i);
			}
		}
		ArrayList<UserThread> playerthreads = togame.getUserThreads();
		for(int i=0; i <playerthreads.size();i++){
			playerthreads.get(i).sendMessage(message);
		}
	}
	
	public void sendtoallexcept(Message message, String uname) {
		String gamename = message.getGamename();
		Game togame=null;
		for(int i=0; i < gamebase.size(); i++){
			if(gamebase.get(i).getGamename().equals(gamename)){
				togame = gamebase.get(i);
			}
		}
		ArrayList<UserThread> playerthreads = togame.getUserThreads();
		for(int i=0; i <playerthreads.size();i++){
			if(!playerthreads.get(i).getUsername().equals(uname)){
				playerthreads.get(i).sendMessage(message);
			}
		}
	}

	public ArrayList<Game> getGamebase() {
		return gamebase;
	}
}

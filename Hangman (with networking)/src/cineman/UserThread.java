package cineman;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UserThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server server;
	private String gamename;
	private String username;
	
	public UserThread(Socket s, Server server) {
		try {
			this.server = server;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioeserverthread: " + ioe.getMessage());
		}
	}

	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioeserverthread: " + ioe.getMessage());
		}
	}

	public void run() {
		try {
			while(true) {
				Message message = (Message)ois.readObject();
				//System.out.println("Uthread Received message of type: " + message.getMessagetype());
				String messagetype =  message.getMessagetype();
				if(messagetype.equals("UniqueGameMessage")){
					checkunique(message);					
				}				
				if(messagetype.equals("NewGameMessage")){
					newgame(message);
				}
				if(messagetype.equals("JoinGameMessage")){
					joingame(message);
				}
				if(messagetype.equals("GameMessage")){
					rungame(message);
				}
				
				
				//server.sendMessageToAllClients(message);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
	
	private void rungame(Message message){
		server.sendtoallexcept(message, message.getUsername());
	}
	
	private void checkunique(Message message){
		UniqueGameMessage ugmessage = (UniqueGameMessage)message;
		String tempgamename = ugmessage.getGamename();
		ArrayList<Game> gamebase = server.getGamebase();
		for(int i=0; i < gamebase.size() ; i++){
			if(gamebase.get(i).getGamename().equals(tempgamename)){
				ugmessage.setUnique(false);
			}
		}			
		sendMessage(ugmessage);
	}
	
	private void newgame(Message message){
		NewGameMessage ngmessage = (NewGameMessage)message;
		String game_name = ngmessage.getGamename();
		String creator = ngmessage.getUsername();
		this.username = creator;//assigning this userthread the respective name
		this.gamename = game_name;//and gamename
		server.addGame(this, creator, game_name, ngmessage.getPlayernum() );
		ArrayList<Game> gamebase = server.getGamebase();
		if(ngmessage.getPlayernum() == 1){
			Game currgame = null;
			for(int i=0; i <gamebase.size(); i++){
				if(gamebase.get(i).getGamename().equals(game_name)){//if exists
					currgame = gamebase.get(i);
				}
			}	
			GameMessage gmessage = new GameMessage(username, game_name, "GameMessage", 1 );
			gmessage.usernames.add(username);
			gmessage.gameword = currgame.getGameword();
			gmessage.makeword();
			server.sendtoallingame(gmessage);			
		}
		
		
	}
	private void joingame(Message message){
		
		JoinGameMessage jgmessage = (JoinGameMessage)message;
		if(jgmessage.getUsername() == null){//if checking if game is valid
			String game_name = jgmessage.getGamename();
			
			boolean validgame = false;
			ArrayList<Game> gamebase = server.getGamebase();
			for(int i=0; i <gamebase.size(); i++){

				if(gamebase.get(i).getGamename().equals(game_name)){//if exists
					if(gamebase.get(i).getWaitingfor() > 0){//if open spots in game
						validgame = true;
					}
				}
			}
			//System.out.println("game name: " + game_name + " : " + validgame);
			jgmessage.setValidgame(validgame);
			sendMessage(jgmessage);
		}
		else{//part 2, checking if username is valid
			String tempuname = jgmessage.getUsername();
			String game_name = jgmessage.getGamename();
			
			boolean validname = false;
			ArrayList<Game> gamebase = server.getGamebase();
			Game currgame = null;
			for(int i=0; i <gamebase.size(); i++){
				if(gamebase.get(i).getGamename().equals(game_name)){//if exists
					currgame = gamebase.get(i);
				}
			}			
			ArrayList<UserThread> players = currgame.getUserThreads();
			boolean temp = true;
			for(int j=0; j < players.size();j++){
				if(players.get(j).getUsername().equals(tempuname)) temp = false;
				//if another player already has the same name
			}
			if(temp) validname = true;//if temp makes it through the for loop unscathed
			
			jgmessage.setValidname(validname);
			sendMessage(jgmessage);
			
			
			if(validname){//adding player to game
				this.gamename = game_name;
				this.username = tempuname;
				currgame.addPlayer(this);
				currgame.setWaitingfor(currgame.getWaitingfor() -1);//decrement waitingfor
				
				ArrayList<String> joinedsofar = new ArrayList<String>();
				for(int i=0; i<players.size();i++){
					joinedsofar.add(players.get(i).getUsername());
				}
				
				NewGameMessage ngmessage = new NewGameMessage(tempuname, game_name, "NewGameMessage", currgame.getPlayernum() );
				ngmessage.setJoinedusers(joinedsofar);
				ngmessage.setWaitingfor(currgame.getWaitingfor());
				server.sendtoallingame(ngmessage);////sending info to all
				if(currgame.getWaitingfor() == 0){
					GameMessage gmessage = new GameMessage(tempuname, game_name, "GameMessage", currgame.getPlayernum() );
					gmessage.usernames = joinedsofar;
					gmessage.gameword = currgame.getGameword();
					gmessage.makeword();
					server.sendtoallingame(gmessage);
				}
			}
			
		}		
		
	}

	public String getGamename() {
		return gamename;
	}

	public String getUsername() {
		return username;
	}
	
}

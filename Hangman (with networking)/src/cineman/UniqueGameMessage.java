package cineman;

public class UniqueGameMessage extends Message {
	//a message class to verify that the provided game name is unique
	private static final long serialVersionUID = 1L;
	
	private boolean unique;

	public UniqueGameMessage(String username, String gamename, String type) {
		super(username, gamename, type);
		unique = true;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

}

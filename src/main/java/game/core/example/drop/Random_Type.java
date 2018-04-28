package game.core.example.drop;

public enum Random_Type {
	random1(1), random2(2), ;
	Random_Type(int type) {
		this.type = type;
	}

	private int type;

	public int getType() {
		return type;
	}
}

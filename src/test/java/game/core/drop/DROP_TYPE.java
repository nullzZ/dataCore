package game.core.drop;

public enum DROP_TYPE {
	dropId(0), // 掉落ID
	item(1), // 道具
	coin(2), // 游戏币
	rune(3), // 钻石
	gouyu(4), // 、勾玉
	power(5), // 体力
	shenghen(6), // 圣痕
	exp(7), // 经验
	soul(8), // 魂
	pet(9), // 精灵ID
	yongqi(10), // 勇气
	;
	private DROP_TYPE(int type) {
		this.type = type;
	}

	private int type;

	public int getType() {
		return type;
	}
}

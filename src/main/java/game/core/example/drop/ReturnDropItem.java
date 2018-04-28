package game.core.example.drop;

public class ReturnDropItem {

	private int id;
	private int num;// 个数
	private int type;

	public ReturnDropItem(int id, int num, int type) {
		this.id = id;
		this.num = num;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public int getNum() {
		return num;
	}

	public int getType() {
		return type;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toLog() {
		StringBuilder sb = new StringBuilder();
		if (this.getType() == DROP_TYPE.coin.getType()) {
			sb.append("金币:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.gouyu.getType()) {
			sb.append("勾玉:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.rune.getType()) {
			sb.append("钻石:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.power.getType()) {
			sb.append("体力:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.exp.getType()) {
			sb.append("经验:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.shenghen.getType()) {
			sb.append("圣痕:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.yongqi.getType()) {
			sb.append("勇气:");
			sb.append(this.getNum());
		} else if (this.getType() == DROP_TYPE.item.getType()) {
			sb.append("物品:");
			sb.append(this.getId() + "x" + this.getNum());
		} else if (this.getType() == DROP_TYPE.pet.getType()) {
			sb.append("宠物:");
			sb.append(this.getId() + "x" + this.getNum());
		}
		return sb.toString();
	}

}

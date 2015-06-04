
public class BarrelLRNode implements ExpNode {

	private ExpNode exp = null;

	public BarrelLRNode(){}

	public BarrelLRNode(ExpNode exp){
		this.exp = exp;
	}

	@Override
	public int evaluate(Robot robot) {
		if(exp != null){
			return robot.getBarrelLR(exp.evaluate(robot));
		}
		return robot.getClosestBarrelLR();
	}

	@Override
	public String toString(){
		if(exp != null){
			StringBuilder str = new StringBuilder();
			str.append("barrelLR(").append(exp.toString()).append(")");
			return str.toString();
		}
		return "barrelLR";
	}

}

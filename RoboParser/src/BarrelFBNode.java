
public class BarrelFBNode implements ExpNode {

	private ExpNode exp = null;

	public BarrelFBNode(){}

	public BarrelFBNode(ExpNode exp){
		this.exp = exp;
	}

	@Override
	public int evaluate(Robot robot) {
		if(exp != null){
			return robot.getBarrelFB(exp.evaluate(robot));
		}
		return robot.getClosestBarrelFB();
	}

	@Override
	public String toString(){
		if(exp != null){
			StringBuilder str = new StringBuilder();
			str.append("barrelFB(").append(exp.toString()).append(")");
			return str.toString();
		}
		return "barrelFB";
	}

}

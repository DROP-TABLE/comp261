
public class GtNode implements CondNode {

	private ExpNode exp1;
	private ExpNode exp2;

	public GtNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public boolean evaluate(Robot robot) {
		if(exp1.evaluate(robot) > exp2.evaluate(robot)) return true;
		return false;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("gt(").append(exp1.toString()).append(", ").append(exp2.toString()).append(")");
		return str.toString();
	}
}


public class SubNode implements ExpNode {

	private ExpNode exp1;
	private ExpNode exp2;

	public SubNode(ExpNode exp1, ExpNode exp2){
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public int evaluate(Robot robot) {
		return exp1.evaluate(robot) - exp2.evaluate(robot);
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("sub(").append(exp1.toString()).append(", ").append(exp2.toString()).append(")");
		return str.toString();
	}


}


public class OrNode implements CondNode {

	private CondNode cond1;
	private CondNode cond2;

	public OrNode(CondNode cond1, CondNode cond2){
		this.cond1 = cond1;
		this.cond2 = cond2;
	}

	@Override
	public boolean evaluate(Robot robot) {
		if(cond1.evaluate(robot) || cond2.evaluate(robot)){
			return true;
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("or(").append(cond1.toString()).append(", ").append(cond2.toString()).append(")");
		return str.toString();
	}

}

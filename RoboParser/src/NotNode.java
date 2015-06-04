
public class NotNode implements CondNode {

	private CondNode cond;

	public NotNode(CondNode cond){
		this.cond = cond;
	}

	@Override
	public boolean evaluate(Robot robot) {
		if(!cond.evaluate(robot)){
			return true;
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("not(").append(cond.toString()).append(")");
		return str.toString();
	}

}

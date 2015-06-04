
public class ElifNode implements RobotProgramNode {

	private BlockNode block;
	private CondNode condition;

	public ElifNode(BlockNode block, CondNode condition){
		this.block = block;
		this.condition = condition;
	}

	@Override
	public void execute(Robot robot) {
		block.execute(robot);
	}

	public boolean checkCondition(Robot robot){
		if(condition.evaluate(robot)) return true;
		return false;
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("elif(\n").append(condition.toString()).append(") {\n").append(block.toString()).append("}\n");
		return str.toString();
	}

}

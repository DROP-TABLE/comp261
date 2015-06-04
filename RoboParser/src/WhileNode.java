
public class WhileNode implements RobotProgramNode {

	private CondNode condition;
	private BlockNode block;

	public WhileNode(CondNode cond, BlockNode block){
		this.condition = cond;
		this.block = block;
	}

	@Override
	public void execute(Robot robot) {
		while(condition.evaluate(robot)){
			block.execute(robot);
		}

	}

	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("while(").append(condition.toString()).append(") {\n").append(block.toString()).append("}\n");

		return str.toString();
	}

}

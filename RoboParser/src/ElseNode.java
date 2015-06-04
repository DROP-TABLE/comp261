
public class ElseNode implements RobotProgramNode {

	private BlockNode block;

	public ElseNode(BlockNode block){
		this.block = block;
	}

	@Override
	public void execute(Robot robot) {
		block.execute(robot);
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("else{\n").append(block.toString()).append("}\n");
		return str.toString();
	}

}

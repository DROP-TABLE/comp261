
public class LoopNode implements RobotProgramNode {

	private BlockNode block;

	public LoopNode(BlockNode block){
		this.block = block;
	}

	@Override
	public void execute(Robot robot) {
		while(true){block.execute(robot);}

	}

	public String toString(){
		return "loop{\n" + block.toString() +"}\n";
	}

}

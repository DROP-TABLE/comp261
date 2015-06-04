import java.util.ArrayList;


public class BlockNode implements RobotProgramNode {

	private ArrayList<RobotProgramNode> statements;

	public BlockNode(){
		statements = new ArrayList<>();
	}


	@Override
	public void execute(Robot robot) {
		for(RobotProgramNode node : statements){
			node.execute(robot);
		}

	}

	public void addStatement(RobotProgramNode stmt){
		statements.add(stmt);
	}

	public String toString(){
		StringBuilder str = new StringBuilder();
		for(RobotProgramNode stmt : statements){
			str.append(stmt.toString());
		}

		return str.toString();
	}

}

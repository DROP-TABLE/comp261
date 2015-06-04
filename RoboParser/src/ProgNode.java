import java.util.ArrayList;

public class ProgNode implements RobotProgramNode {

	private ArrayList<RobotProgramNode> statements;

	public ProgNode(){
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

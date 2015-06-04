
public class TurnRNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.turnRight();

	}

	public String toString(){
		return "turnR;\n";
	}

}

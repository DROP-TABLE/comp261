
public class TurnAroundNode implements RobotProgramNode {



	@Override
	public void execute(Robot robot) {
		robot.turnAround();
	}

	public String toString(){
		return "turnAround;\n";
	}
}

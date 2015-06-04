
public class TurnLNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.turnLeft();

	}

	public String toString(){
		return "turnL;\n";
	}

}

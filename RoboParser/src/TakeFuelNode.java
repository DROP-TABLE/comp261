
public class TakeFuelNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.takeFuel();

	}

	public String toString(){
		return "takeFuel;\n";
	}

}

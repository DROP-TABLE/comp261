
public class ShieldOnNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.setShield(true);
	}

	public String toString(){
		return "shieldOn;\n";
	}

}

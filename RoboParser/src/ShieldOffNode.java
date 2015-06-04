
public class ShieldOffNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.setShield(false);
	}

	public String toString(){
		return "shieldOff;\n";
	}

}

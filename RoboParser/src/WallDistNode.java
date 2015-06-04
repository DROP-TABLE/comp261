
public class WallDistNode implements ExpNode {

	@Override
	public int evaluate(Robot robot) {
		return robot.getDistanceToWall();
	}

	public String toString(){
		return "wallDist";
	}

}

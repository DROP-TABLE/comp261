
public class NumBarrelsNode implements ExpNode {

	@Override
	public int evaluate(Robot robot) {
		return robot.numBarrels();
	}

	@Override
	public String toString(){
		return "numBarrels";
	}

}

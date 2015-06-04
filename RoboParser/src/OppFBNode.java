
public class OppFBNode implements ExpNode {

	@Override
	public int evaluate(Robot robot) {
		return robot.getOpponentFB();
	}

	public String toString(){
		return "oppFB";
	}

}

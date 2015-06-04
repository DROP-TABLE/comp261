
public class OppLRNode implements ExpNode {

	@Override
	public int evaluate(Robot robot) {
		return robot.getOpponentLR();
	}

	public String toString(){
		return "oppLR";
	}

}

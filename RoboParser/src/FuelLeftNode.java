
public class FuelLeftNode implements ExpNode {



	@Override
	public int evaluate(Robot robot) {
		return robot.getFuel();
	}

	public String toString(){
		return "fuelLeft";
	}

}

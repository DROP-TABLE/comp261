
public class NumberNode implements ExpNode {

	private int num;

	public NumberNode(int num){
		this.num = num;
	}

	@Override
	public int evaluate(Robot robot){
		return num;
	}

	@Override
	public String toString(){
		return num + "";
	}
}

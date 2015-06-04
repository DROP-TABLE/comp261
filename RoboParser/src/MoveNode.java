
public class MoveNode implements RobotProgramNode {

	public ExpNode exp = null;

	public MoveNode(){}

	public MoveNode(ExpNode exp){
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		if(exp != null){
			int count = exp.evaluate(robot);
			for(int i=0;i<count;i++){
				robot.move();
			}
		}
		else{
			robot.move();
		}
	}

	public String toString(){
		if(exp != null){
			StringBuilder str = new StringBuilder();
			str.append("move(").append(exp.toString()).append(")\n");
			return str.toString();
		}
		return "move;\n";
	}

}

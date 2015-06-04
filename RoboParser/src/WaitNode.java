
public class WaitNode implements RobotProgramNode {

	public ExpNode exp = null;

	public WaitNode(){}

	public WaitNode(ExpNode exp){
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		if(exp != null){
			int count = exp.evaluate(robot);
			for(int i=0;i<count;i++){
				robot.idleWait();
			}
		}
		else{
			robot.idleWait();
		}
	}

	public String toString(){
		if(exp != null){
			StringBuilder str = new StringBuilder();
			str.append("wait(").append(exp.toString()).append(")\n");
			return str.toString();
		}
		return "wait;\n";
	}

}

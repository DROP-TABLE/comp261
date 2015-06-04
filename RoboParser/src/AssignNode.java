import java.util.Map;


public class AssignNode implements RobotProgramNode {

	private final String identifier;
	private final ExpNode exp;
	private Map<String, ExpNode> vars;

	public AssignNode(String identifier, ExpNode exp, Map<String, ExpNode> vars){
		this.identifier = identifier;
		this.exp = exp;
		this.vars = vars;
	}

	@Override
	public void execute(Robot robot){
		vars.put(identifier, exp);
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(identifier).append(" = ").append(exp.toString()).append(";\n");
		return str.toString();

	}

}

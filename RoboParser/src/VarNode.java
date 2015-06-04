import java.util.Map;


public class VarNode implements ExpNode {

	private String identifier;
	private Map<String, ExpNode> vars;


	public VarNode(String identifier, Map<String, ExpNode> vars){
		this.identifier = identifier;
		this.vars = vars;
	}

	@Override
	public int evaluate(Robot robot){
		if(vars.containsKey(identifier)){
			System.out.println(vars.get(identifier).toString());
			return vars.get(identifier).evaluate(robot);
		}
		return 0;
	}

	@Override
	public String toString(){
		return identifier;
	}

}

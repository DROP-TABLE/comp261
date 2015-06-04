import java.util.ArrayList;
import java.util.List;


public class IfNode implements RobotProgramNode {

	private CondNode condition;
	private BlockNode block;
	private List<ElifNode> elif;
	private ElseNode el = null;

	public IfNode(CondNode cond, BlockNode block, List<ElifNode> elif){
		this.condition = cond;
		this.block = block;
		this.elif = elif;
		this.el = el;
	}

	public IfNode(CondNode cond, BlockNode block, List<ElifNode> elif, ElseNode el){
		this.condition = cond;
		this.block = block;
		this.elif = elif;
		this.el = el;
	}

	@Override
	public void execute(Robot robot) {
		if(condition.evaluate(robot)){
			block.execute(robot);
		}
		else{
			for(ElifNode elifnode : elif){
				if(elifnode.checkCondition(robot)){
					elifnode.execute(robot);
					return;
				}
			}
			if(el != null){
				el.execute(robot);
			}
		}

	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append("if(").append(condition.toString()).append(") {\n").append(block.toString()).append("}\n");
		for(ElifNode elifnode : elif){
			str.append(elifnode.toString());
		}
		if(el != null){
			str.append(el.toString());
		}
		return str.toString();
	}

}

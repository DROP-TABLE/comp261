import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;

import javax.swing.JFileChooser;

/** The parser and interpreter.
    The top level parse function, a main method for testing, and several
    utility methods are provided.
    You need to implement parseProgram and all the rest of the parser.
    */

public class Parser {

    /**
     * Top level parse method, called by the World
     */
    static RobotProgramNode parseFile(File code){
	Scanner scan = null;
	try {
	    scan = new Scanner(code);

	    // the only time tokens can be next to each other is
	    // when one of them is one of (){},;
	    scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

	    RobotProgramNode n = parseProgram(scan);  // You need to implement this!!!

	    scan.close();
	    return n;
	} catch (FileNotFoundException e) {
	    System.out.println("Robot program source file not found");
	} catch (ParserFailureException e) {
	    System.out.println("Parser error:");
	    System.out.println(e.getMessage());
	    scan.close();
	}
	return null;
    }

    /** For testing the parser without requiring the world */

    public static void main(String[] args){
	if (args.length>0){
	    for (String arg : args){
		File f = new File(arg);
		if (f.exists()){
		    System.out.println("Parsing '"+ f+"'");
		    RobotProgramNode prog = parseFile(f);
		    System.out.println("Parsing completed ");
		    if (prog!=null){
			System.out.println("================\nProgram:");
			System.out.println(prog);}
		    System.out.println("=================");
		}
		else {System.out.println("Can't find file '"+f+"'");}
	    }
	} else {
	    while (true){
		JFileChooser chooser = new JFileChooser(".");//System.getProperty("user.dir"));
		int res = chooser.showOpenDialog(null);
		if(res != JFileChooser.APPROVE_OPTION){ break;}
		RobotProgramNode prog = parseFile(chooser.getSelectedFile());
		System.out.println("Parsing completed");
		if (prog!=null){
		    System.out.println("Program: \n"+prog);
		}
		System.out.println("=================");
	    }
	}
	System.out.println("Done");
    }

    // Useful Patterns

    private static Pattern NUMPAT = Pattern.compile("-?(0|[1-9][0-9]*)");  //("-?(0|[1-9][0-9]*)");("-?\\d+");
    private static Pattern VARPAT = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
    private static Pattern OPENPAREN = Pattern.compile("\\(");
    private static Pattern CLOSEPAREN = Pattern.compile("\\)");
    private static Pattern OPENBRACE = Pattern.compile("\\{");
    private static Pattern CLOSEBRACE = Pattern.compile("\\}");

    /**    PROG  ::= STMT+
     */
    static RobotProgramNode parseProgram(Scanner s){
	//THE PARSER GOES HERE!
    	ProgNode prog = new ProgNode();
    	Map<String, ExpNode> vars = new HashMap<>();
    	while(s.hasNext()){
    		prog.addStatement(parseStatement(s, vars));
    	}

	return prog;
    }

    static RobotProgramNode parseStatement(Scanner s, Map<String, ExpNode> vars){
    	if(gobble("move", s)){
    		if(gobble(OPENPAREN, s)){
    			ExpNode exp = parseExp(s, vars);
    			require(CLOSEPAREN, "missing ) in move statement", s);
    			require(";", "missing ; in move statement", s);
    			return new MoveNode(exp);
    		}
    		require(";", "missing ; in move statement", s);
    		return new MoveNode();
    	}
    	if(gobble("turnL", s)){
    		require(";", "missing ; in turnL statement", s);
    		return new TurnLNode();
    	}
    	if(gobble("turnR", s)){
    		require(";", "missing ; in turnR statement", s);
    		return new TurnRNode();
    	}
    	if(gobble("turnAround", s)){
    		require(";", "missing ; in turnAround statement", s);
    		return new TurnAroundNode();
    	}
    	if(gobble("shieldOn", s)){
    		require(";", "missing ; in shieldOn statement", s);
    		return new ShieldOnNode();
    	}
    	if(gobble("shieldOff", s)){
    		require(";", "missing ; in shieldOff statement", s);
    		return new ShieldOffNode();
    	}
    	if(gobble("takeFuel", s)){
    		require(";", "missing ; in takeFuel statement", s);
    		return new TakeFuelNode();
    	}
    	if(gobble("wait", s)){
    		if(gobble(OPENPAREN, s)){
    			ExpNode exp = parseExp(s, vars);
    			require(CLOSEPAREN, "missing ) in wait statement", s);
    			require(";", "missing ; in wait statement", s);
    			return new WaitNode(exp);
    		}
    		require(";", "missing ; in wait statement", s);
    		return new WaitNode();
    	}
    	if(gobble("loop", s)){
    		return parseLoop(s, vars);
    	}
    	if(gobble("if", s)){
    		return parseIf(s, vars);
    	}
    	if(gobble("while", s)){
    		return parseWhile(s, vars);
    	}
    	if(s.hasNext(VARPAT)){
    		String token = s.next();
    		require("=", "missing = in variable declaration statement", s);
    		ExpNode value = parseExp(s, vars);
    		require(";", "missing ; in variable declaration statement", s);
    		return new AssignNode(token, value, vars);
    	}


    	fail("Expression token expected", s);
    	return null;
    }

    private static CondNode parseCond(Scanner s, Map<String, ExpNode> vars){
    	if(gobble("lt", s)){
    		require(OPENPAREN, "missing ( in lt statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in lt statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in lt statement", s);
    		return new LtNode(exp1, exp2);
    	}
    	if(gobble("gt", s)){
    		require(OPENPAREN, "missing ( in gt statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in gt statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in gt statement", s);
    		return new GtNode(exp1, exp2);
    	}
    	if(gobble("eq", s)){
    		require(OPENPAREN, "missing ( in eq statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in eq statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in eq statement", s);
    		return new EqNode(exp1, exp2);
    	}
    	if(gobble("and", s)){
    		require(OPENPAREN, "missing ( in and statement", s);
    		CondNode cond1 = parseCond(s, vars);
    		require(",", "missing , in and statement", s);
    		CondNode cond2 = parseCond(s, vars);
    		require(CLOSEPAREN, "missing ) in and statement", s);
    		return new AndNode(cond1, cond2);
    	}
    	if(gobble("or", s)){
    		require(OPENPAREN, "missing ( in or statement", s);
    		CondNode cond1 = parseCond(s, vars);
    		require(",", "missing , in or statement", s);
    		CondNode cond2 = parseCond(s, vars);
    		require(CLOSEPAREN, "missing ) in or statement", s);
    		return new OrNode(cond1, cond2);
    	}
    	if(gobble("not", s)){
    		require(OPENPAREN, "missing ( in not statement", s);
    		CondNode cond = parseCond(s, vars);
    		require(CLOSEPAREN, "missing ) in not statement", s);
    		return new NotNode(cond);
    	}

    	fail("Condition token expected", s);
    	return null;
    }

    private static ExpNode parseExp(Scanner s, Map<String, ExpNode> vars){
    	if(s.hasNext(VARPAT)){
    		String id = s.next();
    		return new VarNode(id, vars);
    	}
    	if(s.hasNext(NUMPAT)){
    		return new NumberNode(Integer.parseInt(s.next()));
    	}
    	if(gobble("fuelLeft", s)){
    		return new FuelLeftNode();
    	}
    	if(gobble("oppLR", s)){
    		return new OppLRNode();
    	}
    	if(gobble("oppFB", s)){
    		return new OppFBNode();
    	}
    	if(gobble("numBarrels", s)){
    		return new NumBarrelsNode();
    	}
    	if(gobble("barrelLR", s)){
    		if(gobble(OPENPAREN, s)){
    			ExpNode exp = parseExp(s, vars);
    			require(CLOSEPAREN, "missing ) in barrelLR statement", s);
    			return new BarrelLRNode(exp);
    		}
    		return new BarrelLRNode();
    	}
    	if(gobble("barrelFB", s)){
    		if(gobble(OPENPAREN, s)){
    			ExpNode exp = parseExp(s, vars);
    			require(CLOSEPAREN, "missing ) in barrelFB statement", s);
    			return new BarrelFBNode(exp);
    		}
    		return new BarrelFBNode();
    	}
    	if(gobble("wallDist", s)){
    		return new WallDistNode();
    	}
    	if(gobble("add", s)){
    		require(OPENPAREN, "missing ( in add statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in add statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing )  in add statement", s);
    		return new AddNode(exp1, exp2);
    	}
    	if(gobble("sub", s)){
    		require(OPENPAREN, "missing ( in sub statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in sub statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in sub statement", s);
    		return new SubNode(exp1, exp2);
    	}
    	if(gobble("mul", s)){
    		require(OPENPAREN, "missing ( in mul statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in mul statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in mul statement", s);
    		return new MulNode(exp1, exp2);
    	}
    	if(gobble("div", s)){
    		require(OPENPAREN, "missing ( in div statement", s);
    		ExpNode exp1 = parseExp(s, vars);
    		require(",", "missing , in div statement", s);
    		ExpNode exp2 = parseExp(s, vars);
    		require(CLOSEPAREN, "missing ) in div statement", s);
    		return new DivNode(exp1, exp2);
    	}

    	fail("Expression(sensor or number) token expected", s);
    	return null;
    }



    private static RobotProgramNode parseLoop(Scanner s, Map<String, ExpNode> vars){
    	BlockNode block = new BlockNode();
    	require(OPENBRACE, "missing { in loop statement", s);
    	while(!gobble("}", s)){
    		if(!s.hasNext()) fail("Expected } to close loop statement", s);
    		block.addStatement(parseStatement(s, vars));
    	}
    	return new LoopNode(block);
    }

    private static RobotProgramNode parseIf(Scanner s, Map<String, ExpNode> vars){
    	BlockNode block = new BlockNode();
    	List<ElifNode> elif = new ArrayList<>();
    	require(OPENPAREN, "missing ( in if statement", s);
    	CondNode cond = parseCond(s, vars);
    	require(CLOSEPAREN, "missing ) in if statement", s);
    	require(OPENBRACE, "missing { in if statement", s);
    	while(!gobble("}", s)){
    		if(!s.hasNext()) fail("Expected } to close if statement", s);
    		block.addStatement(parseStatement(s, vars));
    	}
    	while(gobble("elif", s)){
    		BlockNode elifBlock = new BlockNode();
    		require(OPENPAREN, "missing ( in elif statement", s);
        	CondNode elifCond = parseCond(s, vars);
        	require(CLOSEPAREN, "missing ) in elif statement", s);
        	require(OPENBRACE, "missing { in elif statement", s);
        	while(!gobble("}", s)){
        		if(!s.hasNext()) fail("Expected } to close elif statement", s);
        		elifBlock.addStatement(parseStatement(s, vars));
        	}
        	elif.add(new ElifNode(elifBlock, elifCond));
    	}
    	if(gobble("else", s)){ //check for following else statement and then process
    		BlockNode elBlock = new BlockNode();
    		require(OPENBRACE, "missing { in else statement", s);
    		while(!gobble("}", s)){
        		if(!s.hasNext()) fail("Expected } to close else statement", s);
        		elBlock.addStatement(parseStatement(s, vars));
        	}

    		ElseNode el = new ElseNode(elBlock);
    		return new IfNode(cond, block, elif, el);
    	}

    	return new IfNode(cond, block, elif);
    }

    private static RobotProgramNode parseWhile(Scanner s, Map<String, ExpNode> vars){
    	BlockNode block = new BlockNode();
    	require(OPENPAREN, "missing ( in while statement", s);
    	CondNode cond = parseCond(s, vars);
    	require(CLOSEPAREN, "missing ) in while statement", s);
    	require(OPENBRACE, "missing { in while statement", s);
    	while(!gobble("}", s)){
    		if(!s.hasNext()) fail("Expected } to close while statement", s);
    		block.addStatement(parseStatement(s, vars));
    	}

    	return new WhileNode(cond, block);
    }




    //utility methods for the parser
    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s){
	String msg = message + "\n   @ ...";
	for (int i=0; i<5 && s.hasNext(); i++){
	    msg += " " + s.next();
	}
	throw new ParserFailureException(msg+"...");
    }

    /**
       If the next token in the scanner matches the specified pattern,
       consume the token and return true. Otherwise return false without
       consuming anything.
       Useful for dealing with the syntactic elements of the language
       which do not have semantic content, and are there only to
       make the language parsable.
     */
    static boolean gobble(String p, Scanner s){
	if (s.hasNext(p)) { s.next(); return true;}
	else { return false; }
    }
    static boolean gobble(Pattern p, Scanner s){
	if (s.hasNext(p)) { s.next(); return true;}
	else { return false; }
    }

    private static String require(Pattern pat, String msg, Scanner s){
    	if(s.hasNext(pat)) return s.next();
    	else{
    		fail(msg, s);
    		return null;
    	}
    }

    private static String require(String pat, String msg, Scanner s){
    	if(s.hasNext(pat)) return s.next();
    	else{
    		fail(msg, s);
    		return null;
    	}
    }

    private static void check(Pattern pat, String msg, Scanner s){
    	if(s.hasNext(pat)) return;
    	else{
    		fail(msg, s);
    		return;
    	}
    }

    private static void check(String pat, String msg, Scanner s){
    	if(s.hasNext(pat)) return;
    	else{
    		fail(msg, s);
    		return;
    	}
    }


}

// You could add the node classes here, as long as they are not declared public (or private)

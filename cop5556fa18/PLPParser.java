package cop5556fa18;

import cop5556fa18.PLPScanner.Token;

import java.util.*;

import cop5556fa18.PLPParser.SyntaxException;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPAST.*;

public class PLPParser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}
	
	PLPScanner scanner;
	Token t;

	PLPParser(PLPScanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}
	
	public Program parse() throws SyntaxException {
//		program();
//		match(Kind.EOF);
		return program();
	}
	
	/*
	 * Program -> Identifier Block
	 */
	public Program program() throws SyntaxException {
		Token temp = t;
		match(Kind.IDENTIFIER);
		Block b = block();
		Program p = new Program(temp, temp.getText(), b);     //not sure
//		matchEOF();           //uncomment this when retesting
		return p;
	}
	
	/*
	 * Block ->  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstDec = { Kind.KW_int, Kind.KW_boolean, Kind.KW_float, Kind.KW_char, Kind.KW_string /* Complete this */ };
	Kind[] firstStatement = {Kind.KW_if, Kind.KW_while, Kind.KW_sleep, Kind.OP_ASSIGN, Kind.KW_print, Kind.IDENTIFIER /*Complete this */  };

	public Block block() throws SyntaxException {
		match(Kind.LBRACE);
		Token temp = t;
		Block b = null;
		List<PLPASTNode> listt = new ArrayList<>();
		PLPASTNode declare = null;
		PLPASTNode statement = null;
		
		
		while (checkKind(firstDec) | checkKind(firstStatement)) {
		    if (checkKind(firstDec)) {
		    	 declare = declaration();
		    	 listt.add(declare);
		    	 
				//declaration();
			} 
		    else if (checkKind(firstStatement)) {
				statement = statement();
				listt.add(statement);
			}
		     	match(Kind.SEMI);
		}
		match(Kind.RBRACE);
		
		b = new Block(temp, listt);
		return b;

	}
	
	public Declaration declaration() throws SyntaxException{
		//TODO
		Kind k = null;
		Token temp = t;
		if(checkKind(Kind.KW_int)) {
			k = Kind.KW_int;
			match(Kind.KW_int);
		}
		else if(checkKind(Kind.KW_boolean)) {
			k = Kind.KW_boolean;
			match(Kind.KW_boolean);
		}
		else if(checkKind(Kind.KW_float)) {
			k = Kind.KW_float;
			match(Kind.KW_float);
		}
		else if(checkKind(Kind.KW_char)) {
			k = Kind.KW_char;
			match(Kind.KW_char);
		}
		else if(checkKind(Kind.KW_string)) {
			k = Kind.KW_string;
			match(Kind.KW_string);
		}
		
		Token x = t;
		List<String> list = new ArrayList<>();
		list.add(x.getText());
		
		match(Kind.IDENTIFIER);
		
		if(checkKind(Kind.OP_ASSIGN)) {
			match(Kind.OP_ASSIGN);
			
			Expression a = expression();
			VariableDeclaration v = new VariableDeclaration(temp, k, x.getText(), a);  
			return v;
		}
		
		else {
			boolean more = true;
			int count = 1;
			
			
			
			while(checkKind(Kind.IDENTIFIER)||checkKind(Kind.COMMA)) {
				if(checkKind(Kind.IDENTIFIER)) {
					
					list.add(t.getText());
					
					match(Kind.IDENTIFIER);
					more = true;
					count++;
				}
				else if(checkKind(Kind.COMMA)) {
					match(Kind.COMMA);
					more = false;
					count--;
				}
			}
			if(!more || count!=1) {
				System.out.println("illegal token: " + t);
				throw new SyntaxException(t,"Syntax Error");
			}
			
			if(list.size()==1) {
				VariableDeclaration va = new VariableDeclaration(temp, k, x.getText(), null);  
				return va;
			}
			else {
				VariableListDeclaration v = new VariableListDeclaration(temp, k, list);
				return v;
			}
			
			
		}
	}
	
	public Statement statement() throws SyntaxException{
		//TODO
		Token temp = t;
		
		Statement s = null;
		
		if(checkKind(Kind.KW_if)) {
			match(Kind.KW_if);
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block b = block();
			s = new IfStatement(temp, e, b);
		}
		//assign
		else if(checkKind(Kind.IDENTIFIER)) {
			match(Kind.IDENTIFIER);
			match(Kind.OP_ASSIGN);
			LHS lhs = null;
			Expression e = expression();
			lhs = new LHS(t, temp.getText());
			s = new AssignmentStatement(temp, lhs, e);
			
		}
		else if(checkKind(Kind.KW_sleep)) {
			match(Kind.KW_sleep);
			Expression e = expression();
			s = new SleepStatement(temp, e);
			
		}
		else if(checkKind(Kind.KW_print)) {
			match(Kind.KW_print);
			Expression e = expression();
			s = new PrintStatement(temp, e);
		}
		else if(checkKind(Kind.KW_while)) {
			match(Kind.KW_while);
			match(Kind.LPAREN);
			Expression e = expression();
			match(Kind.RPAREN);
			Block b = block();
			s = new WhileStatement(temp, e, b);
		}
		else {
			System.out.println("illegal token: " + t);
			throw new SyntaxException(t,"Syntax Error");
		}
		return s;
 
//		throw new UnsupportedOperationException();
	}
	
	public Expression expression() throws SyntaxException{
		Token temp = t;
		Expression cond = null;
		Expression TE = null;
		Expression FE = null;
		cond = OrExpression();
		
		if(checkKind(Kind.OP_QUESTION)) {
			match(Kind.OP_QUESTION);
			TE = expression();
			match(Kind.OP_COLON);
			FE = expression();
			
			cond = new ExpressionConditional(temp, cond, TE, FE);
		}
		return cond;
		
//		throw new UnsupportedOperationException();

	}
	
	//TODO Complete all other productions

	private Expression OrExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k = Kind.OP_OR;
		ExpressionBinary e = null;
		Expression RE = null;
		
		Expression LE = AndExpression();
		while(checkKind(Kind.OP_OR)) {
			match(Kind.OP_OR);
			RE = AndExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression AndExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		ExpressionBinary e = null;
		Kind k = Kind.OP_AND;
		Expression LE = EqExpression();
		Expression RE = null;
		while(checkKind(Kind.OP_AND)) {
			match(Kind.OP_AND);
			RE = EqExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
		
	}

	private Expression EqExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Expression LE = RelExpression();
		Kind k;
		ExpressionBinary e = null;
		Expression RE = null;
		
		while(checkKind(Kind.OP_EQ)||checkKind(Kind.OP_NEQ)) {
			if(checkKind(Kind.OP_EQ)) {
				k = Kind.OP_EQ;
				match(Kind.OP_EQ);
			}
//			else if(checkKind(Kind.OP_NEQ)) {
//				match(Kind.OP_NEQ);
//			}
			else {
				k = Kind.OP_NEQ;
				match(Kind.OP_NEQ);
			}
			RE = RelExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression RelExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k;
		ExpressionBinary e = null;
		Expression RE = null;
		
		Expression LE = AddExpression();
		while(checkKind(Kind.OP_LT)||checkKind(Kind.OP_GT)||checkKind(Kind.OP_GE)||checkKind(Kind.OP_LE)) {
			if(checkKind(Kind.OP_LT)) {
				k = Kind.OP_LT;
				match(Kind.OP_LT);
			}
			else if(checkKind(Kind.OP_GT)) {
				k = Kind.OP_GT;
				match(Kind.OP_GT);
			}
			else if(checkKind(Kind.OP_GE)) {
				k = Kind.OP_GE;
				match(Kind.OP_GE);
			}
//			else if(checkKind(Kind.OP_LE)) {
//				match(Kind.OP_LE);
//			}
			else {
				k = Kind.OP_LE;
				match(Kind.OP_LE);
			}
			RE = AddExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression AddExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k;
		ExpressionBinary e = null;
		Expression RE = null;
		
		Expression LE = MultExpression();
		while(checkKind(Kind.OP_PLUS)||checkKind(Kind.OP_MINUS)) {
			if(checkKind(Kind.OP_PLUS)) {
				k = Kind.OP_PLUS;
				match(Kind.OP_PLUS);
			}
			else if(checkKind(Kind.OP_MINUS)){
				k = Kind.OP_MINUS;
				match(Kind.OP_MINUS);
			}
			else {
				System.out.println("illegal token: " + t);
				throw new SyntaxException(t,"Syntax Error");
			}
			
			RE = MultExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression MultExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k;
		ExpressionBinary e = null;
		Expression RE = null;
		Expression LE = null;
		
		LE = PowerExpression(); 
		while(checkKind(Kind.OP_TIMES)||checkKind(Kind.OP_DIV)||checkKind(Kind.OP_MOD)) {
			if(checkKind(Kind.OP_TIMES)) {
				k = Kind.OP_TIMES;
				match(Kind.OP_TIMES);
			}
			else if(checkKind(Kind.OP_DIV)) {
				k = Kind.OP_DIV;
				match(Kind.OP_DIV);
			}
			else if(checkKind(Kind.OP_MOD)) {
				k = Kind.OP_MOD;
				match(Kind.OP_MOD);
			}
			else {
				System.out.println("illegal token: " + t);
				throw new SyntaxException(t,"Syntax Error");
			}
			RE = PowerExpression();
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression PowerExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k;
		ExpressionBinary e = null;
		Expression RE = null;
		
		Expression LE = UnaryExpression();
		if(checkKind(Kind.OP_POWER)) {
			k = Kind.OP_POWER;
			match(Kind.OP_POWER);
			RE = PowerExpression();
			
			LE = new ExpressionBinary(temp, LE, k, RE);
		}
		return LE;
	}

	private Expression UnaryExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		Kind k;
		ExpressionUnary e = null;
		Expression exp = null;
		
		if(checkKind(Kind.OP_PLUS)) {
			k = Kind.OP_PLUS;
			match(Kind.OP_PLUS);
			exp = UnaryExpression();
			e = new ExpressionUnary(temp, k, exp);
			
		}
		else if(checkKind(Kind.OP_MINUS)) {
			k = Kind.OP_MINUS;
			match(Kind.OP_MINUS);
			exp = UnaryExpression();
			e = new ExpressionUnary(temp, k, exp);
		}
		else if(checkKind(Kind.OP_EXCLAMATION)) {
			k = Kind.OP_EXCLAMATION;
			match(Kind.OP_EXCLAMATION);
			exp = UnaryExpression();
			e = new ExpressionUnary(temp, k, exp);
		}
		else {
			exp = Primary();
			return exp;
		}
		return e;
	}

	private Expression Primary() throws SyntaxException {
		Token temp = t;
		Expression exp = null;
		
		
		// TODO Auto-generated method stub
		if(checkKind(Kind.INTEGER_LITERAL)) {
			exp = new ExpressionIntegerLiteral(temp, Integer.parseInt(t.getText()));
			match(Kind.INTEGER_LITERAL);
		}
		else if(checkKind(Kind.BOOLEAN_LITERAL)) {
			exp = new ExpressionBooleanLiteral(temp, Boolean.parseBoolean(t.getText()));
			match(Kind.BOOLEAN_LITERAL);
			
		}
		else if(checkKind(Kind.FLOAT_LITERAL)) {
			exp = new ExpressionFloatLiteral(temp, Float.parseFloat(t.getText()));
			match(Kind.FLOAT_LITERAL);
			
		}
		else if(checkKind(Kind.CHAR_LITERAL)) {
			exp = new ExpressionCharLiteral(temp, t.getText().charAt(1));
			match(Kind.CHAR_LITERAL);	
		}
		else if(checkKind(Kind.STRING_LITERAL)) {
			String x = t.getText();
			x = x.replace("\"", "");
			exp = new ExpressionStringLiteral(temp, x);
			match(Kind.STRING_LITERAL);
			
		}
		
		else if(checkKind(Kind.LPAREN)) {
			match(Kind.LPAREN);
			exp = expression();
			match(Kind.RPAREN);
		}
		else if(checkKind(Kind.IDENTIFIER)) {
			exp = new ExpressionIdentifier(temp, t.getText());
			match(Kind.IDENTIFIER);

		}
		else {
			exp = Function();
		}
		return exp;
	}

	private Expression Function() throws SyntaxException {
		// TODO Auto-generated method stub
		Token temp = t;
		FunctionWithArg e = null;
		Kind funName = FunctionName();
		match(Kind.LPAREN);
		Expression exp = expression();
		match(Kind.RPAREN);
		e = new FunctionWithArg(temp, funName, exp);
		return e;
	}

	private Kind FunctionName() throws SyntaxException {
		// TODO Auto-generated method stub
		Kind k;
		if(checkKind(Kind.KW_sin)) {
			k = Kind.KW_sin;
			match(Kind.KW_sin);
		}
		else if(checkKind(Kind.KW_cos)) {
			k = Kind.KW_cos;
			match(Kind.KW_cos);
		}
		else if(checkKind(Kind.KW_atan)) {
			k = Kind.KW_atan;
			match(Kind.KW_atan);
		}
		else if(checkKind(Kind.KW_abs)) {
			k = Kind.KW_abs;
			match(Kind.KW_abs);
		}
		else if(checkKind(Kind.KW_log)) {
			k = Kind.KW_log;
			match(Kind.KW_log);
		}
		else if(checkKind(Kind.KW_int)) {
			k = Kind.KW_int;
			match(Kind.KW_int);
		}
		else if(checkKind(Kind.KW_float)) {
			k = Kind.KW_float;
			match(Kind.KW_float);
		}
		else {
			System.out.println("illegal token: " + t);
			throw new SyntaxException(t, "illegal function");
		}
		return k;
	}

	protected boolean checkKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean checkKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}
	
	/**
	 * @param kind
	 * @return 
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		Token temp = t;
		if (kind == Kind.EOF) {
			System.out.println("End of file"); //return t;
		}
		else if (checkKind(kind)) {
			t = scanner.nextToken();
			
		}
		else {
			//TODO  give a better error message!.
			System.out.println("illegal token at: " + t);
			throw new SyntaxException(t,"Syntax Error");
		}
		
		return temp;
	}
	
	private Token matchEOF() throws SyntaxException {
		if (checkKind(Kind.EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}

}

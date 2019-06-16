package cop5556fa18;

import cop5556fa18.PLPAST.AssignmentStatement;
import cop5556fa18.PLPAST.Block;
import cop5556fa18.PLPAST.ExpressionBinary;
import cop5556fa18.PLPAST.ExpressionBooleanLiteral;
import cop5556fa18.PLPAST.ExpressionCharLiteral;
import cop5556fa18.PLPAST.ExpressionConditional;
import cop5556fa18.PLPAST.ExpressionFloatLiteral;
import cop5556fa18.PLPAST.ExpressionIdentifier;
import cop5556fa18.PLPAST.ExpressionIntegerLiteral;
import cop5556fa18.PLPAST.ExpressionStringLiteral;
import cop5556fa18.PLPAST.ExpressionUnary;
import cop5556fa18.PLPAST.FunctionWithArg;
import cop5556fa18.PLPAST.IfStatement;
import cop5556fa18.PLPAST.LHS;
import cop5556fa18.PLPAST.PLPASTVisitor;
import cop5556fa18.PLPAST.PrintStatement;
import cop5556fa18.PLPAST.Program;
import cop5556fa18.PLPAST.SleepStatement;
import cop5556fa18.PLPAST.VariableDeclaration;
import cop5556fa18.PLPAST.VariableListDeclaration;
import cop5556fa18.PLPAST.WhileStatement;

import cop5556fa18.PLPAST.*;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPScanner.Token;
import cop5556fa18.PLPTypes.Type;

public class PLPTypeChecker implements PLPASTVisitor {
	
	PLPTypeChecker() {
	}
	
	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	// Name is only used for naming the output file. 
		// Visit the child block to type check program.
		@Override
		public Object visitProgram(Program program, Object arg) throws Exception {
			program.block.visit(this, arg);
			return null;
		}
		
	symbolTable symboltable = new symbolTable();
		
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symboltable.enterScope();
		
		for(int i=0;i<block.declarationsAndStatements.size();i++) {
			block.declarationsAndStatements(i).visit(this, arg);
		}
		
		
		symboltable.closeScope();
		return null;
		
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitVariableDeclaration(VariableDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type type = null;
		if(declaration.expression!=null) {
			type = (Type) declaration.expression.visit(this, arg);
		}
		Type type2 = PLPTypes.getType(declaration.type);
		
		if((type==type2||type==null) && symboltable.insert(declaration.name, (Declaration) declaration)) {
			
		}
		else {
			throw new SemanticException(declaration.firstToken, "error in declaration");
		}
		
		return null;
	}

	@Override
	public Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(int i=0;i<declaration.names.size();i++) {
			if(symboltable.insert(declaration.names.get(i), (Declaration) declaration)) {
				//declaration.visit(this, arg);
				
			}
			else {
				throw new SemanticException(declaration.firstToken, "error in list declaration");
			}
		}
		
		return null;
		
	}

	@Override
	public Object visitExpressionBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		return PLPTypes.Type.BOOLEAN;
	}

	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e1 = (Type) expressionBinary.leftExpression.visit(this, arg);
		Type e2 = (Type) expressionBinary.rightExpression.visit(this, arg);
		if(e1==Type.INTEGER&&e2==Type.INTEGER&&(expressionBinary.op==Kind.OP_PLUS||expressionBinary.op==Kind.OP_MINUS
				||expressionBinary.op==Kind.OP_TIMES||expressionBinary.op==Kind.OP_DIV||expressionBinary.op==Kind.OP_MOD
				||expressionBinary.op==Kind.OP_POWER||expressionBinary.op==Kind.OP_AND||expressionBinary.op==Kind.OP_OR)) {
			return Type.INTEGER;
		}
		else if(e1==Type.FLOAT&&e2==Type.FLOAT&&(expressionBinary.op==Kind.OP_PLUS||expressionBinary.op==Kind.OP_MINUS
				||expressionBinary.op==Kind.OP_TIMES||expressionBinary.op==Kind.OP_DIV||expressionBinary.op==Kind.OP_POWER)) {
			return Type.FLOAT;
		}
		else if(e1==Type.INTEGER&&e2==Type.FLOAT&&(expressionBinary.op==Kind.OP_PLUS||expressionBinary.op==Kind.OP_MINUS
				||expressionBinary.op==Kind.OP_TIMES||expressionBinary.op==Kind.OP_DIV||expressionBinary.op==Kind.OP_POWER)) {
			return Type.FLOAT;
		}
		else if(e1==Type.FLOAT&&e2==Type.INTEGER&&(expressionBinary.op==Kind.OP_PLUS||expressionBinary.op==Kind.OP_MINUS
				||expressionBinary.op==Kind.OP_TIMES||expressionBinary.op==Kind.OP_DIV||expressionBinary.op==Kind.OP_POWER)) {
			return Type.FLOAT;
		}
		else if(e1==Type.STRING&&e2==Type.STRING&&expressionBinary.op==Kind.OP_PLUS) {
			return Type.STRING;
		}
		else if(e1==Type.BOOLEAN&&e2==Type.BOOLEAN&&(expressionBinary.op==Kind.OP_OR||expressionBinary.op==Kind.OP_AND)) {
			return Type.BOOLEAN;
		}
		else if(e1==Type.INTEGER&&e2==Type.INTEGER&&(expressionBinary.op==Kind.OP_AND||expressionBinary.op==Kind.OP_OR)) {
			return Type.INTEGER;
		}
		else if(e1==Type.INTEGER&&e2==Type.INTEGER&&(expressionBinary.op==Kind.OP_EQ||expressionBinary.op==Kind.OP_NEQ
				||expressionBinary.op==Kind.OP_GT||expressionBinary.op==Kind.OP_GE||expressionBinary.op==Kind.OP_LT
				||expressionBinary.op==Kind.OP_LE)) {
			return Type.BOOLEAN;
		}
		else if(e1==Type.FLOAT&&e2==Type.FLOAT&&(expressionBinary.op==Kind.OP_EQ||expressionBinary.op==Kind.OP_NEQ
				||expressionBinary.op==Kind.OP_GT||expressionBinary.op==Kind.OP_GE||expressionBinary.op==Kind.OP_LT
				||expressionBinary.op==Kind.OP_LE)) {
			return Type.BOOLEAN;
		}
		else if(e1==Type.BOOLEAN&&e2==Type.BOOLEAN&&(expressionBinary.op==Kind.OP_EQ||expressionBinary.op==Kind.OP_NEQ
				||expressionBinary.op==Kind.OP_GT||expressionBinary.op==Kind.OP_GE||expressionBinary.op==Kind.OP_LT
				||expressionBinary.op==Kind.OP_LE)) {
			return Type.BOOLEAN;
		}
		else {
			throw new SemanticException(expressionBinary.firstToken, "error in binary expression");
		}
		
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e1 = (Type)expressionConditional.trueExpression.visit(this, arg);
		Type e2 = (Type)expressionConditional.falseExpression.visit(this, arg);
		if(expressionConditional.condition.visit(this, arg)==Type.BOOLEAN && e1==e2) {
			
		}
		else {
			throw new SemanticException(expressionConditional.firstToken, "error in conditional expression");
		}
		return e1;
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		return PLPTypes.Type.FLOAT;
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFunctionWithArg(FunctionWithArg FunctionWithArg, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e = (Type)FunctionWithArg.expression.visit(this, arg);
		Kind k = FunctionWithArg.functionName;
		
		if(e==Type.INTEGER && k==Kind.KW_abs) {
			return Type.INTEGER;
		}
		else if(e==Type.FLOAT&&(k==Kind.KW_abs||k==Kind.KW_sin||k==Kind.KW_cos||k==Kind.KW_atan||k==Kind.KW_log)) {
			return Type.FLOAT;
		}
		else if(e==Type.INTEGER && k==Kind.KW_float) {
			return Type.FLOAT;
		}
		else if(e==Type.FLOAT && k==Kind.KW_float) {
			return Type.FLOAT;
		}
		else if(e==Type.FLOAT && k==Kind.KW_int) {
			return Type.INTEGER;
		}
		else if(e==Type.INTEGER && k==Kind.KW_int) {
			return Type.INTEGER;
		}
		else {
			throw new SemanticException(FunctionWithArg.firstToken, "error in function");
		}
		
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdentifier expressionIdent, Object arg) throws Exception {
		// TODO Auto-generated method stub
		//PLPTypes type = new PLPTypes();
		if(symboltable.lookup(expressionIdent.name)!=null) {
			if(symboltable.lookup(expressionIdent.name) instanceof VariableDeclaration) {
				expressionIdent.dec = (VariableDeclaration) symboltable.lookup(expressionIdent.name);
				return PLPTypes.getType(expressionIdent.dec.type); 
			}
			else if(symboltable.lookup(expressionIdent.name) instanceof VariableListDeclaration) {
				expressionIdent.decc = (VariableListDeclaration) symboltable.lookup(expressionIdent.name);
				return PLPTypes.getType(expressionIdent.decc.type); 
			}
			else {
				throw new SemanticException(expressionIdent.firstToken, "error in identifier expression");
			}
			
		}
		else {
			throw new SemanticException(expressionIdent.firstToken, "error in identifier expression");
		}
		
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		return PLPTypes.Type.INTEGER;
	}

	@Override
	public Object visitExpressionStringLiteral(ExpressionStringLiteral expressionStringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		return PLPTypes.Type.STRING;
	}

	@Override
	public Object visitExpressionCharLiteral(ExpressionCharLiteral expressionCharLiteral, Object arg) throws Exception {
		// TODO Auto-generated method stub
		return PLPTypes.Type.CHAR;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e1 = (Type) statementAssign.expression.visit(this, arg);
		Type e2 = (Type) statementAssign.lhs.visit(this, arg);
		
		if(e1==e2) {
			return null;
		}
		else {
			throw new SemanticException(statementAssign.firstToken, "error in assignment statement");
		}
		
		
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e = (Type) ifStatement.condition.visit(this, arg);
		if(ifStatement.block!=null) ifStatement.block.visit(this, arg);
		if(!(e==Type.BOOLEAN)) {
			throw new SemanticException(ifStatement.firstToken, "error in if statement");
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e = (Type) whileStatement.condition.visit(this, arg);
		if(whileStatement.b!=null) whileStatement.b.visit(this, arg);
		if(!(e==Type.BOOLEAN)) {
			throw new SemanticException(whileStatement.firstToken, "error in while expression");
		}
		return null;
	}

	@Override
	public Object visitPrintStatement(PrintStatement printStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e = (Type) printStatement.expression.visit(this, arg);
		if(e==Type.BOOLEAN||e==Type.CHAR||e==Type.FLOAT||e==Type.INTEGER||e==Type.STRING) {
			return null;
		}
		else throw new SemanticException(printStatement.firstToken, "error in print expression");
		
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type e = (Type) sleepStatement.time.visit(this, arg);
		if(!(e==Type.INTEGER)) {
			throw new SemanticException(sleepStatement.firstToken, "error in sleep expression");
		}
		return null;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Type type = (Type)expressionUnary.expression.visit(this, arg);
		Kind k = expressionUnary.op;
		if(k==Kind.OP_EXCLAMATION && !(type==Type.INTEGER||type==Type.BOOLEAN)) {
			throw new SemanticException(expressionUnary.firstToken, "error in Unary expression");
		}
		else if((k==Kind.OP_PLUS||k==Kind.OP_MINUS) && !(type==Type.INTEGER||type==Type.FLOAT)){
			throw new SemanticException(expressionUnary.firstToken, "error in Unary expression");
		}
		return (Type)expressionUnary.expression.visit(this, arg);
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		if(symboltable.lookup(lhs.identifier)!=null) {
			//lhs.visit(this, arg);
			if(symboltable.lookup(lhs.identifier) instanceof VariableDeclaration) {
				lhs.dec = (VariableDeclaration) symboltable.lookup(lhs.identifier);			//is a declaration
				return PLPTypes.getType(lhs.dec.type);
			}
			else if(symboltable.lookup(lhs.identifier) instanceof VariableListDeclaration) {
				lhs.decc = (VariableListDeclaration) symboltable.lookup(lhs.identifier);
				return PLPTypes.getType(lhs.decc.type);
			}
			else {
				throw new SemanticException(lhs.firstToken, "error in LHS expression");
			}
			
		}
		else {
			throw new SemanticException(lhs.firstToken, "error in LHS expression");
		}
		
	}

	

}

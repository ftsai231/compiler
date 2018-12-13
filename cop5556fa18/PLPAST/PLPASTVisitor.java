package cop5556fa18.PLPAST;

public interface PLPASTVisitor {
	
	Object visitBlock(Block block, Object arg) throws Exception;
	
	Object visitProgram(Program program, Object arg) throws Exception;
	
	Object visitVariableDeclaration(VariableDeclaration declaration, Object arg) throws Exception;
	
	Object visitVariableListDeclaration(VariableListDeclaration declaration, Object arg) throws Exception;
	
	Object visitExpressionBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception;
	
	Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception;
	
	Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg)throws Exception;
	
	Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception;
	
	Object visitFunctionWithArg(
			FunctionWithArg FunctionWithArg, Object arg)
			throws Exception;
	
	Object visitExpressionIdent(ExpressionIdentifier expressionIdent, Object arg)
			throws Exception;
	
	Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception;
	
	Object visitExpressionStringLiteral(ExpressionStringLiteral expressionStringLiteral, Object arg)
			throws Exception;
	
	Object visitExpressionCharLiteral(ExpressionCharLiteral expressionCharLiteral, Object arg)
			throws Exception;
	
	Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg)
			throws Exception;
	
	Object visitLHS(LHS lhs, Object arg) throws Exception;

	Object visitIfStatement(IfStatement ifStatement, Object arg)
			throws Exception;
	
	Object visitWhileStatement(WhileStatement whileStatement, Object arg)
			throws Exception;
	
	Object visitPrintStatement(PrintStatement printStatement, Object arg)
			throws Exception;
	
	Object visitSleepStatement(SleepStatement sleepStatement, Object arg)
			throws Exception;
	
	Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg)
			throws Exception;
	
	
}

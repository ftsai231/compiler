package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class PrintStatement extends Statement {
	
	public final Expression expression;

	public PrintStatement(Token firstToken, Expression expression) {
		super(firstToken);
		this.expression = expression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitPrintStatement(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrintStatement other = (PrintStatement) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}
	

}

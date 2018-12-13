package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionIntegerLiteral extends Expression {
	
	public final int value;

	public ExpressionIntegerLiteral(Token firstToken, int value) {
		super(firstToken);
		this.value = value;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionIntegerLiteral(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + value;
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
		ExpressionIntegerLiteral other = (ExpressionIntegerLiteral) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	
	
	

}

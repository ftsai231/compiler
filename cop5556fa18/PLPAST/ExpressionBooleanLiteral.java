package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionBooleanLiteral extends Expression {
	
	public final boolean value;

	public ExpressionBooleanLiteral(Token firstToken, boolean value) {
		super(firstToken);
		this.value = value;
	}
	
	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionBooleanLiteral(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (value ? 1231 : 1237);
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
		ExpressionBooleanLiteral other = (ExpressionBooleanLiteral) obj;
		if (value != other.value)
			return false;
		return true;
	}

}

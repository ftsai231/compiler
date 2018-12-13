package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionFloatLiteral extends Expression {
	
	public final float value;

	public ExpressionFloatLiteral(Token firstToken, float value) {
		super(firstToken);
		this.value = value;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionFloatLiteral(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Float.floatToIntBits(value);
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
		ExpressionFloatLiteral other = (ExpressionFloatLiteral) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}
	
	
	

}

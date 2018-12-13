package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionCharLiteral extends Expression {
	
	public final char text;

	public ExpressionCharLiteral(Token firstToken, char text) {
		super(firstToken);
		this.text = text;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionCharLiteral(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + text;
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
		ExpressionCharLiteral other = (ExpressionCharLiteral) obj;
		if (text != other.text)
			return false;
		return true;
	}
	

}

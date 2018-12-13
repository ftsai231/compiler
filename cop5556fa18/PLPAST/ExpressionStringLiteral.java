package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionStringLiteral extends Expression {
	
	public final String text;

	public ExpressionStringLiteral(Token firstToken, String text) {
		super(firstToken);
		this.text = text;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionStringLiteral(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		ExpressionStringLiteral other = (ExpressionStringLiteral) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	
	
	

}

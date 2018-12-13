package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPScanner.Token;

public class ExpressionBinary extends Expression {
	
	public final Expression leftExpression;
	public final Kind op;
	public final Expression rightExpression;
	
	public ExpressionBinary(Token firstToken, Expression leftExpression, Kind op, Expression rightExpression) {
		super(firstToken);
		this.leftExpression = leftExpression;
		this.op = op;
		this.rightExpression = rightExpression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionBinary(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((leftExpression == null) ? 0 : leftExpression.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((rightExpression == null) ? 0 : rightExpression.hashCode());
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
		ExpressionBinary other = (ExpressionBinary) obj;
		if (leftExpression == null) {
			if (other.leftExpression != null)
				return false;
		} else if (!leftExpression.equals(other.leftExpression))
			return false;
		if (op != other.op)
			return false;
		if (rightExpression == null) {
			if (other.rightExpression != null)
				return false;
		} else if (!rightExpression.equals(other.rightExpression))
			return false;
		return true;
	}
}

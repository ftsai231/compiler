package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class ExpressionConditional extends Expression {
	
	public final Expression condition;
	public final Expression trueExpression;
	public final Expression falseExpression;
	
	public ExpressionConditional(Token firstToken, Expression condition, Expression trueExpression,
			Expression falseExpression) {
		super(firstToken);
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionConditional(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((falseExpression == null) ? 0 : falseExpression.hashCode());
		result = prime * result + ((trueExpression == null) ? 0 : trueExpression.hashCode());
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
		ExpressionConditional other = (ExpressionConditional) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (falseExpression == null) {
			if (other.falseExpression != null)
				return false;
		} else if (!falseExpression.equals(other.falseExpression))
			return false;
		if (trueExpression == null) {
			if (other.trueExpression != null)
				return false;
		} else if (!trueExpression.equals(other.trueExpression))
			return false;
		return true;
	}

}

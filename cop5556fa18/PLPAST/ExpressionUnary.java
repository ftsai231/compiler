package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPScanner.Token;

public class ExpressionUnary extends Expression {
	
	public final Kind op;
	public final Expression expression;
	
	public ExpressionUnary(Token firstToken, Kind op, Expression expression) {
		super(firstToken);
		this.op = op;
		this.expression = expression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitExpressionUnary(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
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
		ExpressionUnary other = (ExpressionUnary) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (op != other.op)
			return false;
		return true;
	}
	
	
	

}

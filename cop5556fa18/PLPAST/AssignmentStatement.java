package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class AssignmentStatement extends Statement {
	public final LHS lhs;
	public final Expression expression;
	
	public AssignmentStatement(Token firstToken, LHS lhs, Expression expression) {
		super(firstToken);
		this.lhs = lhs;
		this.expression = expression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitAssignmentStatement(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
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
		AssignmentStatement other = (AssignmentStatement) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		return true;
	}
	

}

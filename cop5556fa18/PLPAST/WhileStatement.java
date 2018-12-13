package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class WhileStatement extends Statement {
	
	public final Expression condition;
	public final Block b;
	
	public WhileStatement(Token firstToken, Expression condition, Block b) {
		super(firstToken);
		this.condition = condition;
		this.b = b;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitWhileStatement(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
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
		WhileStatement other = (WhileStatement) obj;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		return true;
	}
	
	
	

}

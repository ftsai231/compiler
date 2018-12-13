package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class IfStatement extends Statement {
	
	public final Expression condition;
	public final Block block;
	
	public IfStatement(Token firstToken, Expression condition, Block block) {
		super(firstToken);
		this.condition = condition;
		this.block = block;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitIfStatement(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((block == null) ? 0 : block.hashCode());
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
		IfStatement other = (IfStatement) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		return true;
	}

}

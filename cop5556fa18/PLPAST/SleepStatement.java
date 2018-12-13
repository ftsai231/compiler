package cop5556fa18.PLPAST;

import cop5556fa18.PLPScanner.Token;

public class SleepStatement extends Statement {
	
	public final Expression time;

	public SleepStatement(Token firstToken, Expression time) {
		super(firstToken);
		this.time = time;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitSleepStatement(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		SleepStatement other = (SleepStatement) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

}

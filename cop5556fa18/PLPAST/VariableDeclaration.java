package cop5556fa18.PLPAST;

import cop5556fa18.PLPTypes;
import cop5556fa18.PLPScanner.Kind;
import cop5556fa18.PLPTypes.*;
import cop5556fa18.PLPScanner.Token;

public class VariableDeclaration extends Declaration {
	public final Kind type;
	public final String name;
	public final Expression expression;
	public int slot_number;
	public String jvmType;
	
	public VariableDeclaration(Token firstToken, Kind type, String name, Expression expression) {
		super(firstToken);
		this.type = type;
		this.name = name;
		this.expression = expression;
	}

	@Override
	public Object visit(PLPASTVisitor v, Object arg) throws Exception {
		return v.visitVariableDeclaration(this, arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		VariableDeclaration other = (VariableDeclaration) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	public Type getType() { 
		Type typee = PLPTypes.getType(type);
		return typee;
		} 
	
	public void SetJVMType(String jvmTypeDesc) 
	{
		jvmType = jvmTypeDesc;
	}
	
	public String getJVMType() {
		return jvmType;
	}
	
	public int get_current_slot() 
	{
		// TODO Auto-generated method stub
		
		return slot_number;
	}
	
	public void set_current_slot(int slot) {
		// TODO Auto-generated method stub
		slot_number = slot;
	}

}

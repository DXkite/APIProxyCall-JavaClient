package cn.atd3.proxy;

public abstract class ProxyObject {
	Class<?> returnType = null;

	public abstract String getCallUrl();

	public ProxyObject setReturnType(Class<?> returnType) {
		this.returnType = returnType;
		return this;
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}

	public Function method(String name) {
		if (returnType != null) {
			return new Function(this, name, returnType);
		}
		return new Function(this, name);
	}
}

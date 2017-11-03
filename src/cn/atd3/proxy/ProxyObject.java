package cn.atd3.proxy;

public abstract class ProxyObject {

	public abstract String getCallUrl();

	public Function method(String name) {
		return new Function(this, name);
	}

	public Function method(String name, Class<?> returnType) {
		return new Function(this, name,returnType);
	}
}

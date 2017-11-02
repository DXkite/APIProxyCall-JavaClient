package cn.atd3.proxy;

public class Param {
	String name;
	Object object;
	public Param(String name,Object object) {
		this.name=name;
		this.object=object;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
}

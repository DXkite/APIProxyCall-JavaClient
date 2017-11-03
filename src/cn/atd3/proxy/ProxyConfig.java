package cn.atd3.proxy;

public class ProxyConfig {
	protected static ProxyController controller = null;
	protected static Integer timeOut = 3000;
	protected static Integer callid = 0;

	public static ProxyController getController() {
		return controller;
	}
	public static void setController(ProxyController controller) {
		ProxyConfig.controller = controller;
	}
	public static Integer getTimeOut() {
		return timeOut;
	}
	public static void setTimeOut(Integer timeOut) {
		ProxyConfig.timeOut = timeOut;
	}
}

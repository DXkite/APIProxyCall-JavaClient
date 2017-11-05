package cn.atd3.proxy;

import java.net.MalformedURLException;
import java.net.URL;

public class ProxyConfig {
	protected static ProxyController controller = null;
	protected static Integer timeOut = 3000;
	protected static Integer callid = 0;
	protected static String cookiePath = ".remote/cookie";

	public static ProxyController getController() {
		return controller;
	}

	public static String getCookiePath() {
		return cookiePath;
	}

	public static void setCookiePath(String cookiePath) {
		ProxyConfig.cookiePath = cookiePath;
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

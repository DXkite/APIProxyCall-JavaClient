package cn.atd3.test;

import java.io.IOException;

import com.alibaba.fastjson.JSONException;

import cn.atd3.proxy.DefaultController;
import cn.atd3.proxy.ProxyConfig;
import cn.atd3.proxy.ProxyObject;
import cn.atd3.proxy.exception.PermissionException;
import cn.atd3.proxy.exception.ProxyException;
import cn.atd3.proxy.exception.ServerException;

public class TestProxyException {
	static ProxyObject userProxy = new ProxyObject() {
		@Override
		public String getCallUrl() {
			return "http://code4a.atd3.cn/api/v1.0/user";
//			return "http://code4a.atd3.org/api/v1.0/user";
		}
	};
 

	public static void main(String[] args) {
		ProxyConfig.setTimeOut(30000);
		ProxyConfig.setController(new DefaultController());
		// TODO Auto-generated method stub
		try {
			userProxy.method("signin").call("dxkite","dxkite");
		} catch (JSONException | ServerException | IOException | PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProxyException e) {
			System.out.println(e.getCode());
			System.out.println(e.getName());
			System.out.println(e.getMessage());
		}
	}

}

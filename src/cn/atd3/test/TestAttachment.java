package cn.atd3.test;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;

import cn.atd3.proxy.DefaultController;
import cn.atd3.proxy.ProxyConfig;
import cn.atd3.proxy.ProxyObject;
import cn.atd3.proxy.exception.PermissionException;
import cn.atd3.proxy.exception.ServerException;

public class TestAttachment {
	static ProxyObject attachment = new ProxyObject() {
		@Override
		public String getCallUrl() {
			return "http://code4a.atd3.org/dev.php/api/v1.0/attachment";
		}
	};
	public static void main(String[] call) {
		ProxyConfig.setTimeOut(30000);
		ProxyConfig.setController(new DefaultController());
		try {
			attachment.method("debug").call();
			Object obj=attachment.method("list", Attachment.class).call(11);
			System.out.println(obj);
		} catch (JSONException | ServerException | IOException | PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

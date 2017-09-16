package cn.atd3.api.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.atd3.api.proxy.Proxy;
import cn.atd3.api.proxy.ProxyController;
import cn.atd3.api.proxy.ProxyObject;
import cn.atd3.api.proxy.exception.ProxyException;
import cn.atd3.api.proxy.exception.ServerException;

public class Test {
	static List<Cookie> cookies;

	public static void main(String[] args) {

		JSONObject param = new JSONObject();
		cookies = new ArrayList<Cookie>();
		// JSONObject param = new JSONObject();

		Proxy.setController(new ProxyController() {
			@Override
			public List<Cookie> getCookies() {
				return cookies;
			}

			@Override
			public boolean saveCookies(List<Cookie> list) {
				cookies = list;
				return false;
			}

			@Override
			public File saveFile(String mime, String content) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public File getFile(String path) {
				// TODO Auto-generated method stub
				return null;
			}

		});
		try {
			ProxyObject obj = new ProxyObject() {
				@Override
				public String getCallUrl() {
					return "http://safeyd.atd3.org/dx-dev.php/open-api/1.0/user";
				}

			};
			param.put("account", "dxkite");
			param.put("password", "dxkite");

			System.out.println(Proxy.call(obj, "signin", param));
			System.out.println(Proxy.call(obj, "getInfo"));

		} catch (JSONException | ProxyException | ServerException e) {
			e.printStackTrace();
		}
	}
}

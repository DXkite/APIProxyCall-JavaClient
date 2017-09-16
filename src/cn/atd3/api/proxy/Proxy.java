package cn.atd3.api.proxy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.atd3.api.proxy.exception.ProxyException;
import cn.atd3.api.proxy.exception.ServerException;

public class Proxy {
	protected static ProxyController controller = null;
	protected static Integer timeOut = 3000;
	protected static Integer callid = 0;
	protected static CookieStore cookieStore = new BasicCookieStore();

	public static ProxyController getController() {
		return controller;
	}

	public static void setController(ProxyController controller) {
		Proxy.controller = controller;
	}

	public static Integer getTimeOut() {
		return timeOut;
	}

	public static void setTimeOut(Integer timeOut) {
		Proxy.timeOut = timeOut;
	}

	public static JSONObject call(ProxyObject obj, String method, JSONArray param)
			throws JSONException, ServerException {
		JSONObject post = new JSONObject();
		post.put("jsonrpc", "2.0");
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		System.out.println("send_str =>" + post);
		return new JSONObject(downloadJson(obj.getCallUrl(), method, post.toString()));
	}

	public static JSONObject call(ProxyObject obj, String method) throws JSONException, ServerException {
		JSONObject post = new JSONObject();
		post.put("jsonrpc", "2.0");
		post.put("method", method);
		post.put("params", new JSONArray());
		post.put("id", ++callid);
		System.out.println("send_str =>" + post);
		return new JSONObject(downloadJson(obj.getCallUrl(), method, post.toString()));
	}

	public static JSONObject call(ProxyObject obj, String method, JSONObject param)
			throws JSONException, ProxyException, ServerException {
		JSONObject post = new JSONObject();
		post.put("jsonrpc", "2.0");
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		System.out.println("send_str =>" + post);
		return new JSONObject(downloadJson(obj.getCallUrl(), method, post.toString()));
	}

	private static String downloadJson(String callUrl, String method, String content) throws ServerException {
		String response = "";
		HttpPost httpPost = new HttpPost(callUrl);
		List<Cookie> cookies = controller.getCookies();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(cookie);
		}
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		StringEntity entity = new StringEntity(content, "UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		HttpResponse resp = null;
		try {
			resp = client.execute(httpPost);
			controller.saveCookies(cookieStore.getCookies());
		} catch (IOException e) {
			throw new ServerException("Can't get response form server", e);
		}
		if (resp.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity_resposne = resp.getEntity();
			try {
				response = EntityUtils.toString(entity_resposne, "UTF-8");

			} catch (ParseException e) {
				throw new ServerException("Server response decode error", e);
			} catch (IOException e) {
				throw new ServerException("Server response read error", e);
			}
		} else {
			throw new ServerException("Server status error (" + resp.getStatusLine().getStatusCode() + ")");
		}
		return response;
	}

	public static File callFile(ProxyObject obj, String method, JSONObject param) {
		return null;
	}

	protected static String downloadJson(String path, JSONObject post) {
		return null;
	}

	protected static String downloadFile(String path, JSONObject post) {
		return null;
	}

	public static JSONObject packFile(File file) {
		return null;
	}
}

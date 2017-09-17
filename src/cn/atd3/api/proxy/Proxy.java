package cn.atd3.api.proxy;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.atd3.api.proxy.exception.MethodNoFoundException;
import cn.atd3.api.proxy.exception.PermissionException;
import cn.atd3.api.proxy.exception.ProxyException;
import cn.atd3.api.proxy.exception.ServerException;


public class Proxy {
	protected static ProxyController controller = null;
	protected static Integer timeOut = 3000;
	protected static Integer callid = 0;
	protected static CookieStore cookieStore = new BasicCookieStore();
	protected ProxyObject object;
	protected String method;
	protected boolean returnFile=false;
	
	public Proxy(ProxyObject object, String method) {
		this.object = object;
		this.method = method;
		this.returnFile=false;
	}
	
	public Proxy(ProxyObject object, String method,boolean returnFile) {
		this.object = object;
		this.method = method;
		this.returnFile=returnFile;
	}
	
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

	public Object call(Object... params) throws JSONException, ServerException, IOException, PermissionException {
		// check if has file
		for (Object param : params) {
			if (param instanceof File) {
				throw new ProxyException("File params must use Param class to post");
			}
		}
		JSONArray jsonparam = new JSONArray();
		for (Object param : params) {
			jsonparam.put(param);
		}
		return call(jsonparam);
	}

	public Object call(Param... params) throws JSONException, ProxyException, ServerException, IOException, PermissionException {
		boolean hasFile = false;
		// check if has file
		for (Param param : params) {
			if (param.object instanceof File) {
				hasFile = true;
				break;
			}
		}
		if (hasFile) {
			List<Param> list = new ArrayList<Param>();
			for (Param param : params) {
				list.add(param);
			}
			return parseObject(download(this.object.getCallUrl(), method, list));
		} else {
			JSONObject jsonparams = new JSONObject();
			for (Param param : params) {
				jsonparams.put(param.getName(), param.getObject());
			}
			return call(jsonparams);
		}
	}

	public Object call(JSONArray param) throws JSONException, ServerException, IOException, PermissionException {
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}

	public Object call() throws JSONException, ServerException, IOException, PermissionException {
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", new JSONArray());
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}

	public Object call(JSONObject param) throws JSONException, ProxyException, ServerException, IOException, PermissionException {
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}

	private static Object parseObject(Object object) throws JSONException, PermissionException {
		if (object instanceof JSONObject) {
			JSONObject obj = (JSONObject)object;
			System.out.println("parse json object => "+obj);
			if (obj.has("result")) {
				return obj.get("result");
			} else {
				if (obj.has("error")) {
					JSONObject error = obj.getJSONObject("error");
					String name = error.getString("name");
					if("PermissionDeny".equalsIgnoreCase(name)){
						throw new PermissionException(error.getString("message"));
					}else if("MethodNotFound".equalsIgnoreCase(name)){
						throw new MethodNoFoundException(error.getString("message"));
					}else{
						throw new ProxyException(name+":"+error.getString("message"));
					}
				}
			}
		}
		return object;
	}

	private  Object download(String callUrl, String method, String content)
			throws ServerException, JSONException, IOException {
		HttpPost httpPost =null;
		if(returnFile){
			httpPost = new HttpPost(callUrl+"?method="+method);
		}else{
			httpPost = new HttpPost(callUrl);
		}
		List<Cookie> cookies = controller.getCookies();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(cookie);
		}
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		StringEntity entity = new StringEntity(content, "UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		System.out.println("send json =>" + content);
		httpPost.setEntity(entity);
		HttpResponse resp = null;
		try {
			resp = client.execute(httpPost);
			controller.saveCookies(cookieStore.getCookies());
		} catch (IOException e) {
			throw new ServerException("Can't get response form server", e);
		}
		return parseResponse(resp);
	}

	private static Object download(String callUrl, String method, List<Param> params)
			throws ServerException, JSONException, IOException {
		
		HttpPost httpPost = new HttpPost(callUrl+"?method="+method);
		List<Cookie> cookies = controller.getCookies();
		for (Cookie cookie : cookies) {
			cookieStore.addCookie(cookie);
		}
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		for (Param param : params) {
			if (param.object instanceof File) {
				File file=(File) param.object;
				String mime=URLConnection.getFileNameMap().getContentTypeFor(file.getName());
				FileBody filebody=new FileBody(file,ContentType.create(mime));
				System.out.println("upload mime type => " + filebody.getMimeType());
				multipartEntityBuilder.addPart(param.name, filebody);
			} else if (param.object instanceof String) {
				multipartEntityBuilder.addPart(param.name,
						new StringBody((String) param.object, ContentType.TEXT_PLAIN));
			} else {
				multipartEntityBuilder.addPart(param.name,
						new StringBody(param.object.toString(), ContentType.TEXT_PLAIN));
			}
		}
		HttpEntity entity = multipartEntityBuilder.build();
		httpPost.setEntity(entity);
		HttpResponse resp = null;
		try {
			System.out.println("send POST FROM =>" + entity);
			resp = client.execute(httpPost);
			controller.saveCookies(cookieStore.getCookies());
		} catch (IOException e) {
			throw new ServerException("Can't get response form server", e);
		}
		return parseResponse(resp);
	}

	protected static Object parseResponse(HttpResponse resp) throws ServerException, JSONException, IOException {

		if (resp.getStatusLine().getStatusCode() == 200) {
			HttpEntity httpEntity = resp.getEntity();
			// JSON
			if (httpEntity.getContentType().getValue().contains("json")) {
				System.out.println("content is json");
				try {
					String jsonstr=EntityUtils.toString(httpEntity, "UTF-8");
					return new JSONObject(jsonstr);
				} catch (ParseException e) {
					throw new ServerException("Server response decode error", e);
				} catch (IOException e) {
					throw new ServerException("Server response read error", e);
				}
			} else {
				System.out.println("content is not json => "+httpEntity.getContentType().getValue());
				return controller.saveFile(httpEntity.getContentType().getValue(), httpEntity.getContent(),
						httpEntity.getContentLength());
			}
		} else {
			throw new ServerException("Server status error (" + resp.getStatusLine() + ")");
		}
	}
}

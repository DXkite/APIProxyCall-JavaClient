package cn.atd3.api.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
	protected ProxyObject object;
	protected String method;
	protected boolean returnFile = false;

	public Proxy(ProxyObject object, String method) {
		this.object = object;
		this.method = method;
		this.returnFile = false;
	}

	public Proxy(ProxyObject object, String method, boolean returnFile) {
		this.object = object;
		this.method = method;
		this.returnFile = returnFile;
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
	
	/**
	 * 顺序参数调用，不支持文件
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ServerException
	 * @throws IOException
	 * @throws PermissionException
	 */
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
	
	/**
	 * 关联参数调用 支持文件
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ProxyException
	 * @throws ServerException
	 * @throws IOException
	 * @throws PermissionException
	 */
	public Object call(Param... params)
			throws JSONException, ProxyException, ServerException, IOException, PermissionException {
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
	
	/**
	 * 无参数调用
	 * @return
	 * @throws JSONException
	 * @throws ServerException
	 * @throws IOException
	 * @throws PermissionException
	 */
	public Object call() throws JSONException, ServerException, IOException, PermissionException {
		if (returnFile) {
			return parseObject(download(this.object.getCallUrl(), method, new JSONArray().toString()));
		}
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", new JSONArray());
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}
	
	/**
	 * 数组参数调用
	 * @param param
	 * @return
	 * @throws JSONException
	 * @throws ServerException
	 * @throws IOException
	 * @throws PermissionException
	 */
	public Object call(JSONArray param) throws JSONException, ServerException, IOException, PermissionException {
		if (returnFile) {
			return parseObject(download(this.object.getCallUrl(), method, param.toString()));
		}
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}

	/**
	 * 关联参数调用
	 * @param param
	 * @return
	 * @throws JSONException
	 * @throws ProxyException
	 * @throws ServerException
	 * @throws IOException
	 * @throws PermissionException
	 */
	public Object call(JSONObject param)
			throws JSONException, ProxyException, ServerException, IOException, PermissionException {
		if (returnFile) {
			return parseObject(download(this.object.getCallUrl(), method, param.toString()));
		}
		JSONObject post = new JSONObject();
		post.put("method", method);
		post.put("params", param);
		post.put("id", ++callid);
		return parseObject(download(this.object.getCallUrl(), method, post.toString()));
	}

	
	private static Object parseObject(Object object) throws JSONException, PermissionException {
		if (object instanceof JSONObject) {
			JSONObject obj = (JSONObject) object;
			System.out.println("parse json object => " + obj);
			if (obj.has("result")) {
				return obj.get("result");
			} else {
				if (obj.has("error")) {
					JSONObject error = obj.getJSONObject("error");
					String name = error.getString("name");
					if ("PermissionDeny".equalsIgnoreCase(name)) {
						throw new PermissionException(error.getString("message"));
					} else if ("MethodNotFound".equalsIgnoreCase(name)) {
						throw new MethodNoFoundException(error.getString("message"));
					} else {
						throw new ProxyException(name + ":" + error.getString("message"));
					}
				}
			}
		}
		return object;
	}

	private Object download(String callUrl, String method, String content)
			throws ServerException, JSONException, IOException {
		String postAddress = null;
		if (returnFile) {
			postAddress = callUrl + "?method=" + method;
		} else {
			postAddress = callUrl;
		}
		System.out.println("send json => "+content);
		HttpURLConnection httpUrlConnection = createConnection(postAddress);
		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setRequestProperty("Content-Type", "application/json");
		httpUrlConnection.setRequestProperty("Content-Length", String.valueOf(content.getBytes().length));
		httpUrlConnection.connect();
		OutputStream outputStream = httpUrlConnection.getOutputStream();
		outputStream.write(content.getBytes());
		outputStream.flush();
		outputStream.close();
		Object result = parseResponse(httpUrlConnection);
		httpUrlConnection.disconnect();
		return result;
	}

	private Object download(String callUrl, String method, List<Param> params)
			throws ServerException, JSONException, IOException {
		HttpURLConnection httpUrlConnection = createConnection(callUrl+"?method="+method);
		httpUrlConnection.setRequestMethod("POST");
		String boundary = "----ProxyCallFormBoundary" + System.currentTimeMillis()+callid;
		httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+ boundary);
		httpUrlConnection.connect();
		OutputStream outputStream=httpUrlConnection.getOutputStream();
		for (Param param:params){
			outputStream.write(("--"+boundary+"\r\n").getBytes());
			if(param.object instanceof File){
				File file=(File) param.object;
				String contentType=URLConnection.getFileNameMap().getContentTypeFor(file.getName());
				outputStream.write(("Content-Disposition: multipart/form-data; name=\""+param.name+"\"; filename=\""+ file.getName() +"\"\r\n").getBytes());
				outputStream.write(("Content-Type: "+contentType+"\r\n\r\n").getBytes());
				@SuppressWarnings("resource")
				InputStream input = new FileInputStream(file);
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = input.read(bufferOut)) != -1) {
					outputStream.write(bufferOut, 0, bytes);
					outputStream.write(("\r\n").getBytes());
				}
			}else{
				outputStream.write(("Content-Disposition: form-data; name=\""+param.name+"\"\r\n\r\n").getBytes());
				outputStream.write((param.object.toString()+"\r\n").getBytes());
			}
		}
		outputStream.write(("--"+boundary+"\r\n").getBytes());
		outputStream.close();
		Object result = parseResponse(httpUrlConnection);
		httpUrlConnection.disconnect();
		return result;
	}
	
	protected  HttpURLConnection createConnection(String postAddress) throws IOException{
		// 创建连接
		HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(postAddress).openConnection();
		// 设置服务器属性
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setConnectTimeout(timeOut);
		httpUrlConnection.setReadTimeout(timeOut);
		String cookies = controller.getCookies();
		if (cookies != null && !cookies.isEmpty()) {
			httpUrlConnection.setRequestProperty("Cookie", cookies);
		}
		return httpUrlConnection;
	}
	
	protected static Object parseResponse(HttpURLConnection httpUrlConnection) throws ServerException, JSONException, IOException {
		// save cookie
		List<String> cookie_list = httpUrlConnection.getHeaderFields().get("Set-Cookie");
		if(cookie_list!=null){
			for (String cookie : cookie_list) {
				controller.saveCookies(cookie);
			}		
		}
		if (httpUrlConnection.getResponseCode() == 200) {
			// JSON
			if (httpUrlConnection.getContentType().contains("json")) {
				System.out.println("content is json");
				try {

					InputStream inputStream = httpUrlConnection.getInputStream();
					BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
					String l, jsonstr = "";
					while (null != (l = r.readLine())) {
						jsonstr += l + '\n';
					}
					return new JSONObject(jsonstr);
				} catch (IOException e) {
					throw new ServerException("Server response read error", e);
				}
			} else {
				System.out.println("content is not json => " + httpUrlConnection.getContentType());
				return controller.saveFile(httpUrlConnection.getContentType(), httpUrlConnection.getInputStream(),
						httpUrlConnection.getContentLength());
			}
		} else {
			throw new ServerException("Server status error (" + httpUrlConnection.getResponseCode() + ")");
		}
	}
}

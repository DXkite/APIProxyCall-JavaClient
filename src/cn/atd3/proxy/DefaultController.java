package cn.atd3.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DefaultController implements ProxyController {

	final String userCookie = "user.cookie";
	String cookiePath = null;
	Map<String, Map<String, String>> cookieInfo = new HashMap<String, Map<String, String>>();
	MessageDigest md5;

	public DefaultController() {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File filesDir = new File(ProxyConfig.cookiePath);
		if (!filesDir.exists()) {
			filesDir.mkdirs();
		}
	}

	protected boolean initCookie(String url) {
		File filesDir = new File(ProxyConfig.cookiePath);
		if (filesDir.canWrite()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(getCookieFile(url)));
				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.println("load cookie from file:" + line);
					setCookie(url, line);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		} else {
			System.err.println("can't write files directory :" + filesDir.getAbsolutePath());
			return false;
		}
		return true;
	}

	@Override
	public boolean saveCookie(String url, String cookie) {
		setCookie(url, cookie);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getCookieFile(url));
			for (Map.Entry<String, String> cookieMap : getCookieSave(url).entrySet()) {
				String cookieLine = cookieMap.getKey() + "=" + cookieMap.getValue() + ";\r\n";
				writer.write(cookieLine);
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("can't write log:" + e.getMessage());
		}
		return true;
	}

	private void setCookie(String url, String cookie) {
		String cookieStr = null;
		if (cookie.indexOf(";") > 0) {
			cookieStr = cookie.substring(0, cookie.indexOf(";"));
		} else {
			cookieStr = cookie;
		}
		int pos = cookieStr.indexOf("=");
		getCookieSave(url).put(cookieStr.substring(0, pos), cookieStr.substring(pos + 1));
	}

	@Override
	public String getCookies(String url) {
		StringBuffer cookieStr = new StringBuffer();
		for (Map.Entry<String, String> cookie : getCookieSave(url).entrySet()) {
			cookieStr.append(cookie.getKey() + "=" + cookie.getValue() + ";");
		}
		return cookieStr.toString();
	}

	@Override
	public File saveFile(String contentType, InputStream content, long contentLength) throws IOException {
		File temp = File.createTempFile("download-", ".tmp");
		temp.deleteOnExit();
		FileOutputStream out = new FileOutputStream(temp);
		int read = 0;
		byte[] buffer = new byte[1024];
		while ((read = content.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		out.close();
		content.close();
		return temp;
	}

	protected String getCookieId(String url) {
		URL urlparser = null;
		try {
			urlparser = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return userCookie;
		}
		String name = urlparser.getHost();
		md5.update(name.getBytes());
		return bin2hex(md5.digest());
	}

	protected File getCookieFile(String url) {
		File filesDir = new File(ProxyConfig.cookiePath);
		String fileName = getCookieId(url);
		if (filesDir.canWrite()) {
			cookiePath = filesDir.getAbsolutePath() + File.separator + fileName + "-" + userCookie;
			File file = new File(cookiePath);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.err.println("can't create save cookie file");
					return null;
				}
			}
			return file;
		}
		return null;
	}

	protected static String bin2hex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte code : bytes) {
			result.append("0123456789ABCDEF".charAt((code & 0xF0) >> 4));
			result.append("0123456789ABCDEF".charAt(code & 0x0F));
		}
		return result.toString();
	}

	protected Map<String, String> getCookieSave(String url) {
		String id = getCookieId(url);
		if (!cookieInfo.containsKey(id)) {
			cookieInfo.put(id, new HashMap<String, String>());
			initCookie(url);
		}
		return cookieInfo.get(id);
	}
}

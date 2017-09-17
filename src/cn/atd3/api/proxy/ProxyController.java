package cn.atd3.api.proxy;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.http.cookie.Cookie;

public interface ProxyController {
	public List<Cookie> getCookies();

	public boolean saveCookies(List<Cookie> list);

	public File saveFile(String contentType, InputStream content, long contentLength);
}

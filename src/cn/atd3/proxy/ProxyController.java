package cn.atd3.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public interface ProxyController {
	/**
	 * 获取保存的Cookie字符串集合
	 * @return
	 */
	public String getCookies(String url);
	
	/**
	 * 保存从服务器下载的文件
	 * @param contentType 文件MIME类型
	 * @param content 
	 * @param contentLength
	 * @return
	 * @throws IOException 
	 */
	public File saveFile(String contentType, InputStream content, long contentLength) throws IOException;
	
	/**
	 * 保存服务器发送的Cookie字符串
	 * @param cookies
	 * @return
	 */
	boolean saveCookie(String url, String cookie);
}

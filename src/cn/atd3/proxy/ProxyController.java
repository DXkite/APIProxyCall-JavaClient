package cn.atd3.proxy;

import java.io.File;
import java.io.InputStream;


public interface ProxyController {
	/**
	 * 获取保存的Cookie字符串集合
	 * @return
	 */
	public String getCookies();
	
	/**
	 * 保存服务器发送的Cookie字符串
	 * @param cookies
	 * @return
	 */
	public boolean saveCookie(String cookies);

	/**
	 * 保存从服务器下载的文件
	 * @param contentType 文件MIME类型
	 * @param content 
	 * @param contentLength
	 * @return
	 */
	public File saveFile(String contentType, InputStream content, long contentLength);
}

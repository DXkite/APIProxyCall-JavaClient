package cn.atd3.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public abstract class ProxyController {
	/**
	 * 获取保存的Cookie字符串集合
	 * @return
	 */
	abstract public String getCookies(String url);
	
	/**
	 * 保存从服务器下载的文件
	 * @param contentType 文件MIME类型
	 * @param content 
	 * @param contentLength
	 * @return
	 * @throws IOException 
	 */
	abstract public File saveFile(String contentType, InputStream content, long contentLength) throws IOException;
	
	/**
	 * 保存服务器发送的Cookie字符串
	 * @param cookies
	 * @return
	 */
	abstract boolean saveCookie(String url, String cookie);
	
	public boolean clearCookies() {
		File filesDir = new File(ProxyConfig.cookiePath);
		File[] files = filesDir.listFiles();
        for (File cookieFile :files) {
            if (cookieFile.isFile()){
                System.out.println("delete ->  "+ cookieFile.getPath() + " = "+ cookieFile.delete() );
            }
        }
		return filesDir.delete();
	}
}

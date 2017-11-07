package cn.atd3.test;

import java.io.*;
import java.util.*;
import com.alibaba.fastjson.*;
import cn.atd3.proxy.*;
import cn.atd3.proxy.exception.*;

public class Test {
	static Map<String, String> cookie_save;
	final static String downloadFileTestPath = "D:\\some.txt";
	final static String uploadFileTestPath = "E:\\Pictures\\copy_paste.jpg";
	static ProxyObject articleProxy = null;
	static ProxyObject userProxy = null;
	// 初始化
	static {
		// 设置调用的API对象接口
		articleProxy = new ProxyObject() {
			@Override
			public String getCallUrl() {
				return "http://code4a.i.atd3.cn/api/v1.0/article";
//				return "http://code4a.i.atd3.cn/open-api/1.0/article";
			}
		};
		// 设置调用的API对象接口
		userProxy = new ProxyObject() {
			@Override
			public String getCallUrl() {
//				return "http://code4a.atd3.cn/api/1.0/user";
				return "http://code4a.i.atd3.cn/api/v1.0/user";
			}
		};
		// 模拟存储的Cookie
		cookie_save = new HashMap<String, String>();
		// 设置控制器
		ProxyConfig.setController(new DefaultController());
	}

	public static void main(String[] call) {
		ProxyConfig.setTimeOut(30000);
		// 获取文章列表
//		testGetArticleList();
		// 测试登陆
//		testUser();
		// 测试登陆（参数简单化）
//		testUserEasy();
		// 测试设置文章封面（文件上传、权限报错）
//		testSetCoverWithoutSign();
		// 测试设置封面
		testSetCoverWithSign();
		// 测试获取封面（文件下载）
//		testGetCover();
		// 清除cookie
//		System.out.println("clear cookie => "+ProxyConfig.getController().clearCookies());
	}
	
	private static void testGetArticleList() {
		try {
			 
			Object list=articleProxy.method("getList",Article.class).call(1,10);
			if (list instanceof List<?>){
				System.out.println(list);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testGetCover() {

		// 设置参数
		try {
			// 获取封面
			System.out.println("获取封面文件：");
			System.out.println(
					"get cover file => " + new Function(articleProxy, "getCover", true).call(new Param("article", 1)));
//			System.out.println("get cover json => " + new Function(articleProxy, "getCover").call(1));
		} catch (ProxyException | JSONException | ServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionException e) {
			// 权限不足
			System.out.println("permission deny");
			// e.printStackTrace();
		}
	}

	private static void testSetCoverWithSign() {
		// 设置参数
		try {
			// 登陆
			System.out.println("登陆账号：");
//			System.out.println("signin =>" + new Function(userProxy, "signin").call("test", "test_password"));
			System.out.println("signin =>" + new Function(userProxy, "signin").call("dxkite", "dxkite"));
			// 设置封面
			System.out.println("设置封面：");
			System.out.println("set cover => " + new Function(articleProxy, "setCover").call(new Param("article", 1),
					new Param("cover", new File(uploadFileTestPath))));
		} catch (ProxyException | JSONException | ServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionException e) {
			// 权限不足
			System.out.println("permission deny");
			// e.printStackTrace();
		}
	}

	static void testSetCoverWithoutSign() {

		// 设置参数
		try {
			System.out.println("设置封面：");
			System.out.println("return => " + new Function(articleProxy, "setCover").call(new Param("article", 1),
					new Param("cover", new File(uploadFileTestPath))));
		} catch (ProxyException | JSONException | ServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionException e) {
			// 权限不足
			System.out.println("permission deny");
			// e.printStackTrace();
		}
	}

	static void testUserEasy() {
		try {
			// 登陆
			System.out.println("signin =>" + new Function(userProxy, "signin").call("dxkite", "dxkite"));
			// 获取登陆信息
//			System.out.println("获取登陆信息：");
			
			// 适配 JavaBean
			UserInfo userInfo=(UserInfo) userProxy.method("getInfo",UserInfo.class).call();
			System.out.println("get user info=>" + userInfo);
			// 退出登陆
			System.out.println("signout=>" + new Function(userProxy, "signout").call());
			// 尝试不登陆获取信息
//			System.out.println("未登录情况下获取用户信息：");
			System.out.println("get user info=>" + new Function(userProxy, "getInfo").call());
		} catch (JSONException | ProxyException | ServerException | IOException e) {
			e.printStackTrace();
		} catch (PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void testUser() {
		try {
			JSONObject param = new JSONObject();
			param.put("account", "test");
			param.put("password", "test_password");
			// 登陆
			System.out.println("登陆：account=dxkite ,password=dxkite");
			System.out.println("signin =>" + new Function(userProxy, "signin").call(param));
			// 获取登陆信息
			System.out.println("获取登陆信息：");
			System.out.println("get user info=>" + new Function(userProxy, "getInfo").call());
			// 退出登陆
			System.out.println("退出登陆：");
			System.out.println("signout=>" + new Function(userProxy, "signout").call());
			// 尝试不登陆获取信息
			System.out.println("未登录情况下获取用户信息：");
			System.out.println("get user info=>" + new Function(userProxy, "getInfo").call());
		} catch (JSONException | ProxyException | ServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

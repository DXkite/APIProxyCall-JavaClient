package cn.atd3.api.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import cn.atd3.api.proxy.Param;
import cn.atd3.api.proxy.Proxy;
import cn.atd3.api.proxy.ProxyController;
import cn.atd3.api.proxy.ProxyObject;
import cn.atd3.api.proxy.exception.PermissionException;
import cn.atd3.api.proxy.exception.ProxyException;
import cn.atd3.api.proxy.exception.ServerException;

public class Test {
	static List<Cookie> cookies;
	final static String downloadFileTestPath = "D:\\cover.jpg";
	final static String uploadFileTestPath = "D:\\cover.jpg";
	// 初始化
	static {
		// 模拟存储的Cookie
		cookies = new ArrayList<Cookie>();

		// 设置控制器
		Proxy.setController(new ProxyController() {
			@Override
			public List<Cookie> getCookies() {
				// 获取Cookie
				return cookies;
			}

			@Override
			public boolean saveCookies(List<Cookie> list) {
				cookies = list;
				// 储存Cookie
				return true;
			}

			@Override
			public File saveFile(String mime, InputStream content, long contentLength) {
				System.out.println(mime + ":" + contentLength);
				// 模拟保存文件并返回句柄
				return new File(downloadFileTestPath);
			}
		});

	}

	public static void main(String[] args) {
		// 测试登陆
		testUser();
		// 测试登陆（参数简单化）
		testUserEasy();
		// 测试设置文章封面（文件上传、权限报错）
		testSetCoverWithoutSign();
		// 测试设置封面
		testSetCoverWithSign();
		// 测试获取封面（文件下载）
		testGetCover();
	}

	private static void testGetCover() {

		// 设置调用的API对象接口
		ProxyObject articleProxy = new ProxyObject() {
			@Override
			public String getCallUrl() {
				return "http://safeyd.i.atd3.cn/open-api/1.0/article";
			}
		};
		// 设置参数
		try {
			// 获取封面
			System.out.println("获取封面文件：");
			System.out.println("get cover => " + new Proxy(articleProxy, "getCover", true).call(1));
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
		// 设置调用的API对象接口
		ProxyObject userProxy = new ProxyObject() {
			@Override
			public String getCallUrl() {
				return "http://safeyd.i.atd3.cn/open-api/1.0/user";
			}
		};
		// 设置调用的API对象接口
		ProxyObject articleProxy = new ProxyObject() {
			@Override
			public String getCallUrl() {
				return "http://safeyd.i.atd3.cn/open-api/1.0/article";
			}
		};
		// 设置参数
		try {
			// 登陆
			System.out.println("登陆账号：");
			System.out.println("signin =>" + new Proxy(userProxy, "signin").call("dxkite", "dxkite"));
			// 设置封面
			System.out.println("设置封面：");
			System.out.println("set cover => " + new Proxy(articleProxy, "setCover").call(new Param("article", 1),
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
		// 设置调用的API对象接口
		ProxyObject article = new ProxyObject() {
			@Override
			public String getCallUrl() {
				return "http://safeyd.i.atd3.cn/open-api/1.0/article";
			}
		};
		// 设置参数
		try {
			System.out.println("设置封面：");
			System.out.println("return => " + new Proxy(article, "setCover").call(new Param("article", 1),
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
			// 设置调用的API对象接口
			ProxyObject obj = new ProxyObject() {
				@Override
				public String getCallUrl() {
					return "http://safeyd.i.atd3.cn/open-api/1.0/user";
				}

			};
			// 登陆
			System.out.println("登陆：account=dxkite ,password=dxkite");
			System.out.println("signin =>" + new Proxy(obj, "signin").call("dxkite", "dxkite"));
			// 获取登陆信息
			System.out.println("获取登陆信息：");
			System.out.println("get user info=>" + new Proxy(obj, "getInfo").call());
			// 退出登陆
			System.out.println("退出登陆：");
			System.out.println("signout=>" + new Proxy(obj, "signout").call());
			// 尝试不登陆获取信息
			System.out.println("未登录情况下获取用户信息：");
			System.out.println("get user info=>" + new Proxy(obj, "getInfo").call());
		} catch (JSONException | ProxyException | ServerException | IOException e) {
			e.printStackTrace();
		} catch (PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void testUser() {
		try {
			// 设置调用的API对象接口
			ProxyObject obj = new ProxyObject() {
				@Override
				public String getCallUrl() {
					return "http://safeyd.i.atd3.cn/open-api/1.0/user";
				}

			};
			// 参数 account=dxkite password=dxkite
			JSONObject param = new JSONObject();
			param.put("account", "dxkite");
			param.put("password", "dxkite");

			// 登陆
			System.out.println("登陆：account=dxkite ,password=dxkite");
			System.out.println("signin =>" + new Proxy(obj, "signin").call(param));
			// 获取登陆信息
			System.out.println("获取登陆信息：");
			System.out.println("get user info=>" + new Proxy(obj, "getInfo").call());
			// 退出登陆
			System.out.println("退出登陆：");
			System.out.println("signout=>" + new Proxy(obj, "signout").call());
			// 尝试不登陆获取信息
			System.out.println("未登录情况下获取用户信息：");
			System.out.println("get user info=>" + new Proxy(obj, "getInfo").call());
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

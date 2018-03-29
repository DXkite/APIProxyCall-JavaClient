# Extend Remote Procedure Call

## 扩展远程服务调用协议

### 1. 数据类型支持

| 参数类型   | 说明                                  |
| ------ | ----------------------------------- |
| 数字     | 基础类型                                |
| 字符串    | 基础类型                                |
| 原生对象   | 基础类型（序列化到字符串），序列化数据对象，用于相同语言之间的远程调用 |
| JSON对象 | 基础类型                                |
| 文件     | 扩展类型                                |
| 数组     | 基础类型，支持所有基础类型 （JSON对象传递）            |

### 2. 请求协议规则

#### 2.1 包含基础类型的请求包

采用JSON交互，请求包格式如下

```json
{
  "method": remoteCallMethod,
  "params": JSON Oject| JSON Array
  "id"：Integer
}
```

其中，可以指定参数名，采用JSON Object 调用，参数格式如下：

```json
{ "arg1":"xxx","arg2":0,"arg3": {}}
```

##### 2.1.1 调用例子

**采用对象指定参数列表**

```json
{
  "method":"getUserInfo",
  "params":{"userId":1},
  "id":1
}
```

**采用数组指定参数列表**

```json
{
  "method":"getUserInfo",
  "params":[1],
  "id":1
}
```

#### 2.2 包含扩展类型的请求包 (TCP/UDP协议)

当请求参数中包含文件类型的时候，采用封包调试

```
XRPC-Method: remoteCallMethod <CR><LF>
XRPC-Id: remoteCallId <CR><LF>
XRPC-Boundary : Split Boundary =<Boundary> <CR><LF>
XRPC-Params: <ParamNumbers> <CR><LF>
<CR><LF>
Content-Disposition: MIME-Type ; name= "paramName"; filename = "fileName" <CR><LF>
<CR><LF>
<DATA><CR><LF>
--<Boundary><CR><LF>
Content-Disposition: MIME-Type ; name= "paramName";<CR><LF>
<CR><LF>
<DATA><CR><LF>
--<Boundary><CR><LF>
```

#### 2.3 包含扩展采用HTTP协议的封包

采用 HTTP协议  Form-Data 数据格式封包

### 3. 返回协议规则

#### 3.1 正常返回包（基础类型）

```json
{
  "result" : ReturnValue
  "id" : Integer
}
```

#### 3.2 扩展类型返回包（TCP/UDP）

```
XRPC-Id: remoteCallId
Content-Disposition: MIME-Type ; name= "paramName"; filename = "fileName" <CR><LF>
Content-Length: <Length> <CR><LF>
<CR><LF>
<DATA>
```

#### 3.3 扩展类型返回包（HTTP）

采用HTTP协议封包

#### 3.4 错误信息提示

```json
{
  "error": {
    "name": ErrorName,
    "code": ErrorCode,
    "message": ErrorMessage,
     "data" :{
       "file": ErrorFile,
       "line": ErrorLine,
       "backtrace" : BackTrace
     }
  },
  "id": Integer
}
```

## 注意事项

在HTTP协议中，充分利用HTTP协议来控制调用，而不要单纯的使用JSON来描述
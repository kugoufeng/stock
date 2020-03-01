package cn.jeremy.stock.tools;

import cn.jeremy.stock.autotrade.LoadRequestElement;
import cn.jeremy.stock.autotrade.SendMail;
import cn.jeremy.stock.bean.HttpResult;
import cn.jeremy.stock.bean.RequestElement;
import cn.jeremy.stock.exception.ExitException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * http访问工具
 *
 * @author kugoufeng
 */
public class HttpTools
{

    private static Logger LOGGER = LogManager.getLogger(HttpTools.class);

    /**
     * http的header中的content-type属性的名字
     */
    private static final String CONTENT_TYPE_NAME = "content-type";

    /**
     * content-type:text/xml; charset=UTF-8
     */
    private static final String CONTENT_TYPE_VALUE_XML_UTF_8 = "text/xml; charset=UTF-8";

    /**
     * http的header中的content-type属性的传输类型
     */
    private static final String TEXT_XML = "text/xml";

    /**
     * http的header中的content-type属性的字符编码
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * HttpClient实例
     */
    private HttpClient httpClient = getHttpClient();

    /**
     * 链接的超时数,默认为5秒,此处要做成可配置（单位为毫秒）
     */
    private static final int CONNECTION_TIME_OUT = 500;

    /**
     * 每个主机的最大并行链接数，默认为2
     */
    private static final int MAX_CONNECTIONS_PER_HOST = 10;

    /**
     * 客户端总并行链接最大数，默认为20
     */
    private static final int MAX_TOTAL_CONNECTIONS = 20;

    private static HttpTools instance = new HttpTools();

    /**
     * 获取类的实例化对象
     *
     * @return cn.jeremy.stock.tools.HttpTools
     */
    public static HttpTools getInstance()
    {
        return instance;
    }

    /**
     * 设置请求头，headers格式为xx:xx|xx:xx
     *
     * @param httpMethod 类HttpMethod
     * @param postMethod 类PostMethod
     * @param getMethod 类GetMethod
     * @param headers 请求头
     */
    private void setHeaders(EntityEnclosingMethod httpMethod, PostMethod postMethod, GetMethod getMethod,
        String headers)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("set request headers, headers:{}", headers);
        }

        if (Util.isNotEmpty(headers))
        {
            String[] hs = headers.split("\\|");
            if (hs.length > 0)
            {
                for (String h : hs)
                {
                    String[] kv = h.split("\\:");
                    if (kv.length > 2 && (kv[1].equals("http") || kv[1].equals("https")))
                    {
                        kv = new String[] {kv[0], kv[1].concat(":").concat(kv[2])};
                    }
                    if (kv.length == 2)
                    {
                        if (httpMethod != null)
                        {
                            httpMethod.addRequestHeader(kv[0], kv[1]);
                        }
                        else if (postMethod != null)
                        {
                            postMethod.addRequestHeader(kv[0], kv[1]);
                        }
                        else if (getMethod != null)
                        {
                            getMethod.addRequestHeader(kv[0], kv[1]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 发送http请求，并返回响应的xml报文
     *
     * @param url 请求地址
     * @param xml 访问报文
     * @param headers 请求头
     * @return cn.jeremy.stock.bean.HttpResult
     */
    public HttpResult sendRequest(String url, String xml, String headers, String respCharset)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("send http request, url:{}, xml:{}, headers:{}", url, xml, headers);
        }
        HttpResult resp = null;
        EntityEnclosingMethod httpMethod = new PostMethod(url);
        this.setHeaders(httpMethod, null, null, headers);
        try
        {
            // 设置header信息，传输XML格式的
            httpMethod.setRequestHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_VALUE_XML_UTF_8);
            // 发送含xml消息体的对象
            RequestEntity entity = new StringRequestEntity(xml, TEXT_XML, UTF_8);
            httpMethod.setRequestEntity(entity);
            // 处理响应结果码
            int resultCode = httpClient.executeMethod(httpMethod);
            resp = getHttpResult(resultCode, respCharset, httpMethod);
        }
        catch (Exception ex)
        {
            LOGGER.error("request has error, url:{}, xml:{}, headers:{}, e:{}", url, xml, headers, ex);
        }
        finally
        {
            httpMethod.releaseConnection();
        }
        return resp;
    }

    /**
     * 根据result设置响应结果
     *
     * @param resultCode 响应码
     * @param httpMethod 带用响应体的请求对象
     * @return cn.jeremy.stock.bean.HttpResult
     */
    private HttpResult getHttpResult(int resultCode, String charset, HttpMethodBase httpMethod)
        throws IOException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("enter, resultCode:{}", resultCode);
        }
        HttpResult httpResult = new HttpResult();
        if (HttpStatus.SC_OK == resultCode)
        {

            InputStream inStream = httpMethod.getResponseBodyAsStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream(4096);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inStream.read(buffer)) > 0)
            {
                outStream.write(buffer, 0, len);
            }
            outStream.close();

            // 获取响应报文
            byte[] resBody = outStream.toByteArray();
            // 响应报文
            String responseXml;
            if (null == resBody || 0 == resBody.length)
            {
                responseXml = httpMethod.getResponseBodyAsString();
            }
            else
            {
                responseXml = new String(resBody, charset);
            }

            httpResult.setRespCode(HttpStatus.SC_OK);
            httpResult.setResponseBody(StringTools.unicodeToString(responseXml));
        }
        else if (HttpStatus.SC_MOVED_TEMPORARILY == resultCode || HttpStatus.SC_MOVED_PERMANENTLY == resultCode)
        {
            httpResult.setRespCode(HttpStatus.SC_MOVED_TEMPORARILY);
            Header locationHeader = httpMethod.getResponseHeader("location");
            if (null != locationHeader)
            {
                httpResult.setRedirectLocation(locationHeader.getValue());
            }
        }
        else
        {
            httpResult.setRespCode(resultCode);
        }

        httpResult.setHeaders(httpMethod.getResponseHeaders());
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("exit, httpResult:{}", httpResult);
        }
        return httpResult;
    }

    /**
     * 以post的方式发送http请求
     *
     * @param url 请求地址
     * @param postParams 访问参数
     * @param headers 请求头地址
     * @return cn.jeremy.stock.bean.HttpResult
     */
    public HttpResult sendRequestByPost(String url, Map<String, String> postParams, String headers, String respCharset)
    {

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("send http request, url:{}, postParams:{}, headers:{}", url, postParams, headers);
        }

        PostMethod postMethod = new PostMethod(url)
        {
            @Override
            public String getRequestCharSet()
            {
                return "UTF-8";
            }
        };
        this.setHeaders(null, postMethod, null, headers);
        postMethod.setFollowRedirects(false);
        HttpResult resp = new HttpResult();
        try
        {
            if (null != postParams && !postParams.isEmpty())
            {
                Set<String> keys = postParams.keySet();
                for (String key : keys)
                {
                    if (Util.isEmpty(postParams.get(key)))
                    {
                        continue;
                    }
                    postMethod.addParameter(key, postParams.get(key));
                }
            }

            int resultCode = httpClient.executeMethod(postMethod);
            resp = getHttpResult(resultCode, respCharset, postMethod);
        }
        catch (Exception ex)
        {
            LOGGER.error("request has error, url:{}, postParams:{}, headers:{}, e:{}", url, postParams, headers, ex);
        }
        finally
        {
            postMethod.releaseConnection();
            httpClient.getState().clearCookies();
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("exit, resp:{}", resp);
        }
        return resp;
    }

    /**
     * post方式发送http请求，响应码不为200，则记录错误日志，并发送邮件
     *
     * @param url 请求地址
     * @param postParams 请求参数
     * @param headers 请求头
     * @param mailSubject 邮件主题
     * @param isFinish 是否接受程序的运行
     * @return cn.jeremy.stock.bean.HttpResult
     */
    public HttpResult sendRequestByPost(String url, Map<String, String> postParams, String headers, String mailSubject,
        boolean isFinish, String respCharset)
    {
        HttpResult result = sendRequestByPost(url, postParams, headers, respCharset);
        //请求不成功，记录错误日志
        if (result.getRespCode() != HttpStatus.SC_OK)
        {
            LOGGER.error(
                "request failed,mailSubject:{}, url:{}, reqHeaders:{}, postParams:{}, httpResult:{}, isFinish:{}",
                mailSubject,
                url,
                headers,
                postParams,
                result,
                isFinish);
            StringBuffer sb = new StringBuffer("request failed, url:");
            sb.append(url).append("\r\n\r\n");
            sb.append("reqHeaders:").append(headers).append("\r\n\r\n");
            sb.append("postParams:").append(postParams).append("\r\n\r\n");
            sb.append("httpResult:").append(result);
            if (Util.isNotEmpty(mailSubject))
            {
                SendMail.sendMail(mailSubject, sb.toString(), null);
            }
            if (isFinish)
            {
                throw new ExitException(-99, "exit program");
            }
        }

        return result;
    }

    /**
     * 发送get请求
     *
     * @param url 请求地址
     * @param headers 请求头
     * @return cn.jeremy.stock.bean.HttpResult
     */
    public HttpResult sendHttpRequestByGet(String url, String headers, String respCharset)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("send http request, url:{},  headers:{}", url, headers);
        }
        HttpResult resp = new HttpResult();
        GetMethod getMethod = new GetMethod(url);
        this.setHeaders(null, null, getMethod, headers);
        getMethod.setFollowRedirects(false);

        try
        {
            int resultCode = httpClient.executeMethod(getMethod);
            resp = getHttpResult(resultCode, respCharset, getMethod);
        }
        catch (Throwable e)
        {
            LOGGER.error("request has error, url:{}, headers:{}, e:{}", url, headers, e);
        }
        finally
        {
            getMethod.releaseConnection();
            httpClient.getState().clearCookies();
        }
        return resp;
    }

    /**
     * 发送http请求，得到结果
     *
     * @param requestElementKey http请求元素的key
     * @return cn.jeremy.stock.bean.HttpResult
     * @author fengjiangtao
     */
    public HttpResult sendHttpRequest(String requestElementKey)
    {
        RequestElement requestElement = LoadRequestElement.getRequestElement(requestElementKey);
        return sendHttpRequest(requestElement);
    }

    /**
     * 发送http请求，得到结果
     *
     * @param requestElement http请求元素对象
     * @return cn.jeremy.stock.bean.HttpResult
     * @author fengjiangtao
     */
    public HttpResult sendHttpRequest(RequestElement requestElement)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("enter, requestElement:{}", requestElement);
        }
        if (Util.isEmpty(requestElement))
        {
            return null;
        }
        HttpResult result;
        if (requestElement.isPost())
        {
            result = sendRequestByPost(requestElement.getUrl(),
                requestElement.getRequestParams(),
                requestElement.getHeaders(),
                requestElement.getMailSubject(),
                requestElement.isFinish(), requestElement.getRespCharset());
        }
        else
        {
            result = sendHttpRequestByGet(requestElement.getUrl(),
                requestElement.getHeaders(),
                requestElement.getRespCharset());
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("exit, result:{}", result);
        }

        return result;
    }

    /**
     * 发送http请求，返回响应的cookie值
     *
     * @param requestElementKey http请求元素的key
     * @return java.lang.String
     * @author fengjiangtao
     */
    public String getCookie(String requestElementKey)
    {
        RequestElement requestElement = LoadRequestElement.getRequestElement(requestElementKey);
        return getCookie(requestElement);
    }

    /**
     * 发送http请求，返回响应的cookie值
     *
     * @param requestElement http请求元素的key
     * @return java.lang.String
     * @author fengjiangtao
     */
    public String getCookie(RequestElement requestElement)
    {
        if (Util.isEmpty(requestElement))
        {
            return null;
        }
        HttpResult result = sendHttpRequest(requestElement);
        return getCookies(result.getHeaders(), requestElement.getRemoveCookieKeys());
    }

    /**
     * 保留cookies，其他删除
     *
     * @param cookieNames cookie的名称
     */
    public void retainCookies(String... cookieNames)
    {
        if (null == cookieNames)
        {
            httpClient.getState().clearCookies();
            return;
        }

        Cookie[] cookies = httpClient.getState().getCookies();
        ArrayList<Cookie> retainCookies = new ArrayList<Cookie>();
        for (Cookie cookie : cookies)
        {
            for (String retCookie : cookieNames)
            {
                if (cookie.getName().equalsIgnoreCase(retCookie))
                {
                    retainCookies.add(cookie);
                }
            }

        }
        httpClient.getState().clearCookies();
        httpClient.getState().addCookies(retainCookies.toArray(new Cookie[0]));
    }

    /**
     * 删除指定cookies
     *
     * @param cookieNames cookie的名称
     */
    public void removeCookies(String... cookieNames)
    {
        if (null == cookieNames)
        {
            return;
        }

        Cookie[] cookies = httpClient.getState().getCookies();
        ArrayList<Cookie> retainCookies = new ArrayList<Cookie>();

        for (Cookie cookie : cookies)
        {
            for (String rmCookie : cookieNames)
            {
                // 这里是“非”
                if (!cookie.getName().equalsIgnoreCase(rmCookie))
                {
                    retainCookies.add(cookie);
                }
            }

        }
        httpClient.getState().clearCookies();
        httpClient.getState().addCookies(retainCookies.toArray(new Cookie[0]));
    }

    /**
     * 构造Http客户端对象
     *
     * @return org.apache.commons.httpclient.HttpClient
     */
    private HttpClient getHttpClient()
    {
        // 此处运用连接池技术。
        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();

        // 设定参数：与每个主机的最大连接数
        manager.getParams().setDefaultMaxConnectionsPerHost(MAX_CONNECTIONS_PER_HOST);

        // 设定参数：客户端的总连接数
        manager.getParams().setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);

        // 使用连接池技术创建HttpClient对象
        HttpClient httpClient = new HttpClient(manager);

        // 设置超时时间
        httpClient.getParams().setConnectionManagerTimeout(CONNECTION_TIME_OUT);

        return httpClient;
    }

    /**
     * 找出响应头中的cookie，并设置成所有的
     *
     * @param headers 响应头
     * @param removeKey 不包含的cookie值
     * @return java.lang.String
     * @author fengjiangtao
     */
    public String getCookies(Header[] headers, List<String> removeKey)
    {
        StringBuffer sb = new StringBuffer();
        if (Util.isEmpty(headers))
        {
            return null;
        }

        for (int i = 0; i < headers.length; i++)
        {
            Header header = headers[i];
            String headerName = header.getName();
            String headerValue = header.getValue();
            if (!StringTools.isEq(headerName, "Set-Cookie") || Util.isEmpty(headerValue))
            {
                continue;
            }

            String[] headerValues = headerValue.split(";");
            if (Util.isNotEmpty(removeKey) && removeKey.contains(headerValues[0].split("=")[0]))
            {
                continue;
            }
            sb.append(headerValues[0]).append("; ");
        }

        return sb.toString();
    }

    /**
     * 获取指定的cookie，并转换为字符串
     *
     * @param headers 响应头
     * @param key cookie的key
     * @return java.lang.String
     * @author fengjiangtao
     */
    public String getCookieValue(Header[] headers, String key)
    {
        if (Util.isEmpty(headers))
        {
            return null;
        }

        for (int i = 0; i < headers.length; i++)
        {
            Header header = headers[i];
            String headerName = header.getName();
            String headerValue = header.getValue();
            if (!StringTools.isEq(headerName, "Set-Cookie") || Util.isEmpty(headerValue))
            {
                continue;
            }

            String[] headerValues = headerValue.split(";");
            String[] split = headerValues[0].split("=");
            if (StringTools.isEq(split[0].toUpperCase(), key.toUpperCase()))
            {
                return split[1];
            }
        }

        return null;
    }
}

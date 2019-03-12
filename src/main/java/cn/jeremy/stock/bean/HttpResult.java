package cn.jeremy.stock.bean;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 封装http响应数据的bean
 *
 * @author kugoufeng
 * @date 2017/12/19 下午 9:38
 */
public class HttpResult
{

    public HttpResult()
    {
        super();
    }

    public HttpResult(int respCode, String responseBody, String redirectLocation)
    {
        this.respCode = respCode;
        this.responseBody = responseBody;
        this.redirectLocation = redirectLocation;
    }

    /**
     * 响应的状态码
     */
    private int respCode;

    /**
     * 响应的响应体
     */
    private String responseBody;

    /**
     * 重定向路径
     */
    private String redirectLocation;

    /**
     * 响应的响应头
     */
    private Header[] headers;

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
            .append("respCode", respCode)
            .append("responseBody", responseBody)
            .append("redirectLocation", redirectLocation)
            .append("headers", headers)
            .toString();
    }

    public Header[] getHeaders()
    {
        return headers;
    }

    public void setHeaders(Header[] headers)
    {
        this.headers = headers;
    }

    public int getRespCode()
    {
        return respCode;
    }

    public void setRespCode(int respCode)
    {
        this.respCode = respCode;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }

    public String getRedirectLocation()
    {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation)
    {
        this.redirectLocation = redirectLocation;
    }

}

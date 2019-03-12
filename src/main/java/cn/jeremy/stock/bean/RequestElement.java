package cn.jeremy.stock.bean;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * http请求对象元素
 *
 * @author fengjiangtao
 * @date 2018/1/8 下午 10:48
 */
public class RequestElement
{
    /** 请求地址*/
    private String url;

    /** 请求的参数*/
    private Map<String, String> requestParams;

    /** 请求头xx:xx|xx:xx*/
    private String headers;

    /** 邮件主题*/
    private String mailSubject;

    /** 响应码不为200时，是否结束程序运行*/
    private boolean isFinish;

    /** Cookie中要排除的字段*/
    private List<String> removeCookieKeys;

    /** true为post请求，false为get请求*/
    private boolean isPost;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Map<String, String> getRequestParams()
    {
        return requestParams;
    }

    public void setRequestParams(Map<String, String> requestParams)
    {
        this.requestParams = requestParams;
    }

    public String getHeaders()
    {
        return headers;
    }

    public void setHeaders(String headers)
    {
        this.headers = headers;
    }

    public String getMailSubject()
    {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject)
    {
        this.mailSubject = mailSubject;
    }

    public boolean isFinish()
    {
        return isFinish;
    }

    public void setFinish(boolean finish)
    {
        isFinish = finish;
    }

    public List<String> getRemoveCookieKeys()
    {
        return removeCookieKeys;
    }

    public void setRemoveCookieKeys(List<String> removeCookieKeys)
    {
        this.removeCookieKeys = removeCookieKeys;
    }

    public boolean isPost()
    {
        return isPost;
    }

    public void setPost(boolean post)
    {
        isPost = post;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
            .append("url", url)
            .append("requestParams", requestParams)
            .append("headers", headers)
            .append("mailSubject", mailSubject)
            .append("isFinish", isFinish)
            .append("removeCookieKeys", removeCookieKeys)
            .append("isPost", isPost)
            .toString();
    }
}

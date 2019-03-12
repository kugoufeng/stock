package cn.jeremy.stock.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示邮件类，你需要设置：账户名和密码、收件人、抄送(可选)、暗送(可选)、主题、内容，以及附件(可选) 在创建了Mail对象之后 可以调用它的setSubject()、setContent()，设置主题和正文
 * 也可以调用setFrom()和 addToAddress()，设置发件人，和添加收件人。 也可以调用addAttch()添加附件 创建AttachBean：new AttachBean(new File("..."),
 * "fileName");
 *
 * @author kugoufeng
 * @date 2017/12/21 上午 9:14
 */
public class Mail
{
    /**
     * 发件人
     */
    private String from;

    /**
     * 收件人
     */
    private StringBuilder toAddress = new StringBuilder();

    /**
     * 抄送
     */
    private StringBuilder ccAddress = new StringBuilder();

    /**
     * 暗送
     */
    private StringBuilder bccAddress = new StringBuilder();

    /**
     * 主题
     */
    private String subject;

    /**
     * 正文
     */
    private String content;

    /**
     * 附件列表
     */
    private List<AttachBean> attachList = new ArrayList<AttachBean>();

    /**
     * 无参构造
     */
    public Mail()
    {
    }

    /**
     * 有参构造
     *
     * @param from 发件人
     * @param to 收件人
     */
    public Mail(String from, String to)
    {
        this(from, to, null, null);
    }

    /**
     * 有参构造
     *
     * @param from 发件人
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    public Mail(String from, String to, String subject, String content)
    {
        this.from = from;
        this.toAddress.append(to);
        this.subject = subject;
        this.content = content;
    }

    /**
     * 设置发件人
     *
     * @param from 发件人
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /**
     * 返回发件人
     *
     * @return java.lang.String
     */
    public String getFrom()
    {
        return from;
    }

    /**
     * 返回主题
     *
     * @return java.lang.String
     */
    public String getSubject()
    {
        return subject;
    }

    /**
     * 设置主题
     *
     * @param subject 主题
     */
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    /**
     * 得到邮件主题内容
     *
     * @return java.lang.String
     */
    public String getContent()
    {
        return content;
    }

    /**
     * 设置邮件主题内容
     *
     * @param content 主题内容
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * 获取收件人
     *
     * @return java.lang.String
     */
    public String getToAddress()
    {
        return toAddress.toString();
    }

    /**
     * 获取抄送地址
     *
     * @return java.lang.String
     */
    public String getCcAddress()
    {
        return ccAddress.toString();
    }

    /**
     * 获取暗送地址
     *
     * @return java.lang.String
     */
    public String getBccAddress()
    {
        return bccAddress.toString();
    }

    /**
     * 添加收件人,可以是多个收件人
     *
     * @param to 收件人
     */
    public void addToAddress(String to)
    {
        if (this.toAddress.length() > 0)
        {
            this.toAddress.append(",");
        }
        this.toAddress.append(to);
    }

    /**
     * 添加抄送人，可以是多个抄送人
     *
     * @param cc 抄送人
     */
    public void addCcAddress(String cc)
    {
        if (this.ccAddress.length() > 0)
        {
            this.ccAddress.append(",");
        }
        this.ccAddress.append(cc);
    }

    /**
     * 添加暗送人，可以是多个暗送人
     *
     * @param bcc 暗送人
     */
    public void addBccAddress(String bcc)
    {
        if (this.bccAddress.length() > 0)
        {
            this.bccAddress.append(",");
        }
        this.bccAddress.append(bcc);
    }

    /**
     * 添加附件，可以添加多个附件
     *
     * @param attachBean 附件对象
     */
    public void addAttach(AttachBean attachBean)
    {
        this.attachList.add(attachBean);
    }

    /**
     * 获取所有附件
     *
     * @return java.util.List<cn.jeremy.stock.bean.AttachBean>
     */
    public List<AttachBean> getAttachs()
    {
        return this.attachList;
    }

    /**
     * 重写toString
     *
     * @return java.lang.String
     */
    @Override
    public String toString()
    {
        return "Mail [from=" + from + ", toAddress=" + toAddress + ", ccAddress=" + ccAddress + ", bccAddress="
            + bccAddress + ", subject=" + subject + ", content=" + content + ", attachList=" + attachList + "]";
    }

}

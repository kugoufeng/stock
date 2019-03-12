package cn.jeremy.stock.autotrade;

import cn.jeremy.stock.bean.AttachBean;
import cn.jeremy.stock.bean.Mail;
import cn.jeremy.stock.tools.DateTools;
import cn.jeremy.stock.tools.MailTools;
import cn.jeremy.stock.tools.PropertiesTools;
import cn.jeremy.stock.tools.Util;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import javax.mail.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 用于发送邮件
 *
 * @author kugoufeng
 * @date 2017/12/25 上午 10:52
 */
public class SendMail
{
    private static final Logger LOGGER = LogManager.getLogger(SendMail.class);

    /**
     * 邮箱代理地址
     */
    private static final String host = PropertiesTools.getProperty("mailHost");

    /**
     * 发送邮箱账号名称
     */
    private static final String sendAccountName = PropertiesTools.getProperty("sendAccountName");

    /**
     * 发送邮箱账号密码
     */
    private static final String sendAccountPass = PropertiesTools.getProperty("sendAccountPassword");

    /**
     * 接收邮箱账号名称
     */
    private static final String acceptAccountName = PropertiesTools.getProperty("acceptAccountName");

    /**
     * 创建发送邮箱账户
     */
    private static Session session = MailTools.createSession(host, sendAccountName, sendAccountPass);

    /**
     * 邮件发送频率限制
     */
    private static volatile Map<String, Long> sendFrequencyLimit = new HashMap<>();

    /**
     * 获取发送账户session
     *
     * @author fengjiangtao
     */
    public static Session getSession()
    {
        if (Util.isEmpty(session))
        {
            session = MailTools.createSession(host, sendAccountName, sendAccountPass);
        }
        return session;
    }

    /**
     * 发送邮件
     *
     * @param subject
     * @param content
     * @param attachList
     * @author fengjiangtao
     */
    public static void sendMail(String subject, String content, List<AttachBean> attachList)
    {
        Long timeStamp = sendFrequencyLimit.get(subject);
        long currentTimeLong = DateTools.getCurrentTimeLong();
        if (Util.isNotEmpty(timeStamp) && (AutoTrade.sendMailFrequencyLimitValve * 60 * 1000) >
            DateTools.getTimeDifference(timeStamp, currentTimeLong))
        {
            return;
        }
        sendFrequencyLimit.put(subject, currentTimeLong);
        Mail mail = new Mail(sendAccountName, acceptAccountName, subject, content);
        if (Util.isNotEmpty(attachList))
        {
            mail.getAttachs().addAll(attachList);
        }
        try
        {
            MailTools.send(getSession(), mail);
        }
        catch (Exception e)
        {
            LOGGER.error("mai:{}, error:{}", mail, e);
        }
    }

}

package cn.jeremy.stock.tools;

import cn.jeremy.stock.bean.AttachBean;
import cn.jeremy.stock.bean.Mail;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * 邮件工具类
 *
 * @author kugoufeng
 * @date 2017/12/21 下午 6:34
 */
public class MailTools
{
    /**
     * 创建邮件链接session
     *
     * @param host 邮件服务器域名
     * @param username 邮箱账户
     * @param password 邮箱密码
     * @return javax.mail.Session
     */
    public static Session createSession(String host, final String username, final String password)
    {
        Properties prop = new Properties();
        // 指定主机
        prop.setProperty("mail.host", host);
        // 指定验证为true
        prop.setProperty("mail.smtp.auth", "true");
        // 创建验证器
        Authenticator auth = new Authenticator()
        {
            @Override
            public PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        };

        // 获取session对象
        return Session.getInstance(prop, auth);
    }

    /**
     * 发送指定的邮件
     *
     * @param session 邮箱连接session
     * @param mail 邮件对象
     */
    public static void send(Session session, final Mail mail)
        throws MessagingException, IOException
    {
        // 创建邮件对象
        MimeMessage msg = new MimeMessage(session);
        // 设置发件人
        msg.setFrom(new InternetAddress(mail.getFrom()));
        // 设置收件人
        msg.addRecipients(RecipientType.TO, mail.getToAddress());

        // 设置抄送
        String cc = mail.getCcAddress();
        if (!cc.isEmpty())
        {
            msg.addRecipients(RecipientType.CC, cc);
        }

        // 设置暗送
        String bcc = mail.getBccAddress();
        if (!bcc.isEmpty())
        {
            msg.addRecipients(RecipientType.BCC, bcc);
        }
        // 设置主题
        msg.setSubject(mail.getSubject());
        // 创建部件集对象
        MimeMultipart parts = new MimeMultipart();
        // 创建一个部件
        MimeBodyPart part = new MimeBodyPart();
        // 设置邮件文本内容
        part.setContent(mail.getContent(), "text/html;charset=utf-8");
        // 把部件添加到部件集中
        parts.addBodyPart(part);
        // 添加附件
        List<AttachBean> attachBeanList = mail.getAttachs();
        // 获取所有附件
        if (attachBeanList != null)
        {
            for (AttachBean attach : attachBeanList)
            {
                // 创建一个部件
                MimeBodyPart attachPart = new MimeBodyPart();
                // 设置附件文件
                attachPart.attachFile(attach.getFile());
                // 设置附件文件名
                attachPart.setFileName(MimeUtility.encodeText(attach.getFileName()));
                String cid = attach.getCid();
                if (cid != null)
                {
                    attachPart.setContentID(cid);
                }
                parts.addBodyPart(attachPart);
            }
        }
        // 给邮件设置内容
        msg.setContent(parts);
        // 发邮件
        Transport.send(msg);
    }
}

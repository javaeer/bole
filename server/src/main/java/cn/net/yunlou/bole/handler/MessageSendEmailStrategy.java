package cn.net.yunlou.bole.handler;

import cn.net.yunlou.bole.common.MessageEntity;
import cn.net.yunlou.bole.common.constant.MessageSendType;
import cn.net.yunlou.bole.config.AppEmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * FileName: MessageSendEmailStrategy Description: Created By laughtiger Created At 2025/12/25 00:02
 * Modified By Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSendEmailStrategy implements IMessageSendStrategy {

    private final AppEmailConfig appEmailConfig;

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;

    @Override
    public boolean send(MessageEntity message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(getFromAddress()); // 与腾讯企业账户邮箱一致
            helper.setTo(message.getTo());
            helper.setSubject(message.getSubject());
            helper.setText(message.getText(), true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // throw new RuntimeException(e);
            return false;
        }

        return true;
    }

    /** 发送 HTML 邮件 */
    public void sendHtmlEmail(String to, String subject, String htmlContent)
            throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@yourdomain.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true 表示发送 HTML

        javaMailSender.send(message);
    }

    /** 发送带附件的邮件 */
    public void sendEmailWithAttachment(
            String to, String subject, String text, String attachmentPath)
            throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("noreply@yourdomain.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // 添加附件
        File file = new File(attachmentPath);
        helper.addAttachment(file.getName(), file);

        javaMailSender.send(message);
    }

    /** 使用 Thymeleaf 模板发送邮件 */
    public void sendTemplateEmail(String to, String subject, String templateName, Context context)
            throws MessagingException {
        String htmlContent = templateEngine.process(templateName, context);
        sendHtmlEmail(to, subject, htmlContent);
    }

    /** 获取发件人地址（带名称） */
    @SneakyThrows
    private InternetAddress getFromAddress() {
        String address = appEmailConfig.getFrom().getAddress();
        String name = appEmailConfig.getFrom().getName();

        if (StringUtils.hasText(name)) {
            return new InternetAddress(address, name, "UTF-8");
        }
        return new InternetAddress(address);
    }

    @Override
    public boolean supports(MessageSendType sendType) {
        return MessageSendType.EMAIL == sendType;
    }
}

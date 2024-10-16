package Capston.CosmeticTogether.global.auth.service;

import Capston.CosmeticTogether.global.auth.dto.EmailAuthResponseDto;
import Capston.CosmeticTogether.global.auth.dto.MailDTO;
import Capston.CosmeticTogether.global.auth.dto.request.DuplicateDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    public EmailAuthResponseDto sendEmail(DuplicateDTO.Email mailDTO) {
        if (redisUtil.existData(mailDTO.getEmail())) {
            redisUtil.deleteData(mailDTO.getEmail());
        }

        try {
            MimeMessage emailForm = createEmailForm(mailDTO.getEmail());
            mailSender.send(emailForm);
            return new EmailAuthResponseDto(true, "인증번호가 메일로 전송되었습니다.");
        } catch (MessagingException | MailSendException e) {
            return new EmailAuthResponseDto(false, "메일 전송 중 오류가 발생하였습니다. 다시 시도해주세요.");
        }
    }

    private MimeMessage createEmailForm(String email) throws MessagingException {

        String authCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("화함 인증코드");

        String body = "";
        body += "<div style='background-color: #f9f9f9; padding: 20px; font-family: Arial, sans-serif;'>";
        body += "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); padding: 20px;'>";
        body += "<h1 style='text-align: center; color: #333333;'>안녕하세요, 화함입니다.</h1>";
        body += "<p style='text-align: center; color: #555555; font-size: 16px;'>회원가입 인증을 완료하기 위해 아래의 인증 코드를 사용하세요.</p>";
        body += "<div style='text-align: center; margin: 20px 0; padding: 20px; border-radius: 8px; background-color: #f1f1f1; border: 1px solid #dddddd;'>";
        body += "<h2 style='color: #007BFF; margin-bottom: 0;'>인증 코드</h2>";
        body += "<p style='font-size: 24px; font-weight: bold; color: #333333; margin: 10px 0;'>CODE: <strong>" + authCode + "</strong></p>";
        body += "</div>";
        body += "<p style='text-align: center; color: #555555; font-size: 14px;'>감사합니다!</p>";
        body += "</div>";
        body += "</div>";

        message.setContent(body, "text/html; charset=utf-8");

        redisUtil.setDataExpire(email, authCode, 3 * 60L);

        return message;
    }

    public EmailAuthResponseDto validateAuthCode(String email, String authCode) {
        String findAuthCode = redisUtil.getData(email);
        if (findAuthCode == null) {
            return new EmailAuthResponseDto(false, "인증번호가 만료되었습니다. 다시 시도해주세요.");
        }

        if (findAuthCode.equals(authCode)) {
            return new EmailAuthResponseDto(true, "인증 성공에 성공했습니다.");

        } else {
            return new EmailAuthResponseDto(false, "인증번호가 일치하지 않습니다.");
        }
    }
}

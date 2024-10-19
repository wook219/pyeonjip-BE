package com.team5.pyeonjip.user.service;

import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.user.dto.MailDto;
import com.team5.pyeonjip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SendEmailService {

    private final UserService userService;

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "hth130598@gmail.com";


    public MailDto createMailAndChangePassword(String email, String name) {

        String tempPassword = userService.getTempPassword();
        if (tempPassword == null || tempPassword.isEmpty()) {
            throw new GlobalException(ErrorCode.TEMP_PASSWORD_GENERATION_FAILED);
        }

        MailDto dto = new MailDto();
        dto.setAddress(email);
        dto.setTitle(name + "님의 편집 임시비밀번호 안내 메일입니다.");
        dto.setMessage("안녕하세요.\n편집 임시비밀번호 안내 관련 메일입니다.\n\n" + "[" + name + "]" +"님의 임시 비밀번호는 "
                + tempPassword + " 입니다.");

        try {
            userService.updatePassword(email, tempPassword);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.USER_UPDATE_FAILED);
        }
        return dto;
    }


    public void mailSend(MailDto dto) {

        String cleanedEmail = dto.getAddress().trim();

        if (!isValidEmail(cleanedEmail)) {
            throw new GlobalException(ErrorCode.EMAIL_SEND_FAILED);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(cleanedEmail);
        message.setFrom(FROM_ADDRESS);
        message.setSubject(dto.getTitle());
        message.setText(dto.getMessage());

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new GlobalException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}

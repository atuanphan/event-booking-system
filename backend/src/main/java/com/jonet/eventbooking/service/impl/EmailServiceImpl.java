package com.jonet.eventbooking.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{
	private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String emailAddress;

	@Override
	public void sendEmailRegisterSuccess(String toEmail) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailAddress);
		message.setTo(toEmail);
		message.setText("Tài khoản của bạn đã được đăng ký ");
		message.setSubject(emailAddress);
		javaMailSender.send(message);
	}

	@Override
	public void sendEmailResetPassword(String toEmail, String password) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(emailAddress);
		message.setTo(toEmail);
		message.setText("Mật khẩu của bạn đã được reset.Mật khẩu mới là:" + password);
		message.setSubject(emailAddress);
		javaMailSender.send(message);
	}

}

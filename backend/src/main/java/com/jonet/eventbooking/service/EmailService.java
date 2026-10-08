package com.jonet.eventbooking.service;

public interface EmailService {
	void sendEmailRegisterSuccess(String toEmail);
	void sendEmailResetPassword(String toEmail, String password);
}

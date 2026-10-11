package com.jonet.eventbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
	@NotBlank(message = "Mật khẩu hiện tại là bắt buộc")
	private String currentPassword;

	@NotBlank(message = "Mật khẩu mới là bắt buộc")
	private String newPassword;
}

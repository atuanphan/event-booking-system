package com.jonet.eventbooking.dto.request.user;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBatchRequest {
	
	@Valid
	@NotEmpty(message = "list must not be empty!")
	public List<UserRequest> userRequests;
}

package com.jonet.eventbooking.dto;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class UploadResult {
    private String url;
    private String publicId;
}

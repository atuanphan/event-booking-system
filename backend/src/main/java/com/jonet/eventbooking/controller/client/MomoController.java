package com.jonet.eventbooking.controller.client;

import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.payment.MomoQueryRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController 
@RequiredArgsConstructor 
@RequestMapping("/v1/payment/momo")
public class MomoController {

    @PostMapping("/ipn")
    public ResponseEntity<?> ipnUrl(@RequestBody MomoQueryRequest request) {
        return null;
    }
    
}

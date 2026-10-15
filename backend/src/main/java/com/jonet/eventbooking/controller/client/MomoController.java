package com.jonet.eventbooking.controller.client;

import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.payment.MomoReturnRequest;
import com.jonet.eventbooking.service.MomoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController 
@RequiredArgsConstructor 
@RequestMapping("/v1/payment/momo")
public class MomoController {
    private final MomoService momoService;

    @PostMapping("/ipn")
    public ResponseEntity<Void> ipnUrl(@RequestBody MomoReturnRequest request) {
        momoService.handleIpn(request);
        return ResponseEntity.noContent().build();
    }
    
}

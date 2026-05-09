package com.jonet.eventbooking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID>{

}

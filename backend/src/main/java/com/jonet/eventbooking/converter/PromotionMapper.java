package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;

import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;
import com.jonet.eventbooking.entity.PromotionEntity;

@Mapper (componentModel = "spring")
public interface PromotionMapper {

    PromotionResponse toResponse(PromotionEntity entity);
}

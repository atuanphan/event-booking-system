package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;
import com.jonet.eventbooking.entity.PromotionEntity;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionResponse toResponse(PromotionEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "orderEntities", ignore = true)
    PromotionEntity toEntity(PromotionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "orderEntities", ignore = true)
    void updateEntity(PromotionRequest request, @MappingTarget PromotionEntity entity);
}

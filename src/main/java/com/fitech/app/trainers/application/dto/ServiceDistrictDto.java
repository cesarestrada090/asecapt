package com.fitech.app.trainers.application.dto;

import com.fitech.app.trainers.domain.entities.ServiceDistrict;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
@Schema(description = "DTO for service coverage districts")
public class ServiceDistrictDto {
    @Schema(description = "District ID", example = "1")
    private Integer id;
    
    @Schema(description = "District name", example = "Miraflores")
    private String districtName;
    
    @Schema(description = "City name", example = "Lima")
    private String city;
    
    @Schema(description = "State or region", example = "Lima")
    private String state;
    
    @Schema(description = "Country", example = "Perú")
    private String country;
    
    @Schema(description = "District creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    public static ServiceDistrictDto fromEntity(ServiceDistrict entity) {
        ServiceDistrictDto dto = new ServiceDistrictDto();
        dto.setId(entity.getId());
        dto.setDistrictName(entity.getDistrictName());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public ServiceDistrict toEntity() {
        ServiceDistrict entity = new ServiceDistrict();
        entity.setId(this.getId());
        entity.setDistrictName(this.getDistrictName());
        entity.setCity(this.getCity());
        entity.setState(this.getState());
        entity.setCountry(this.getCountry());
        return entity;
    }
} 
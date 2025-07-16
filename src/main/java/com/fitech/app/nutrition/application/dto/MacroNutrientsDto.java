package com.fitech.app.nutrition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroNutrientsDto {
    private BigDecimal proteins;      // gramos
    private BigDecimal carbohydrates; // gramos
    private BigDecimal fats;          // gramos
    private BigDecimal calories;      // kcal
    private BigDecimal fiber;         // gramos
    private BigDecimal sugar;         // gramos
    private BigDecimal sodium;        // mg
} 
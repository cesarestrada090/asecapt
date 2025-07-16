package com.fitech.app.trainers.application.dto;

import com.fitech.app.nutrition.application.dto.MacroNutrientsDto;

import java.util.List;

public class MacroCalculationResponseDto {
    private MacroNutrientsDto totalMacros;
    private List<FoodCalculationDetailDto> foodDetails;
    private MacroPercentagesDto percentages;

    public MacroCalculationResponseDto() {}

    public MacroCalculationResponseDto(MacroNutrientsDto totalMacros, 
                                     List<FoodCalculationDetailDto> foodDetails,
                                     MacroPercentagesDto percentages) {
        this.totalMacros = totalMacros;
        this.foodDetails = foodDetails;
        this.percentages = percentages;
    }

    public MacroNutrientsDto getTotalMacros() {
        return totalMacros;
    }

    public void setTotalMacros(MacroNutrientsDto totalMacros) {
        this.totalMacros = totalMacros;
    }

    public List<FoodCalculationDetailDto> getFoodDetails() {
        return foodDetails;
    }

    public void setFoodDetails(List<FoodCalculationDetailDto> foodDetails) {
        this.foodDetails = foodDetails;
    }

    public MacroPercentagesDto getPercentages() {
        return percentages;
    }

    public void setPercentages(MacroPercentagesDto percentages) {
        this.percentages = percentages;
    }

    public static class FoodCalculationDetailDto {
        private Long foodId;
        private String foodName;
        private String category;
        private Double quantity;
        private Double totalWeight;
        private MacroNutrientsDto macros;

        public FoodCalculationDetailDto() {}

        public FoodCalculationDetailDto(Long foodId, String foodName, String category, 
                                      Double quantity, Double totalWeight, MacroNutrientsDto macros) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.category = category;
            this.quantity = quantity;
            this.totalWeight = totalWeight;
            this.macros = macros;
        }

        public Long getFoodId() {
            return foodId;
        }

        public void setFoodId(Long foodId) {
            this.foodId = foodId;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public Double getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(Double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public MacroNutrientsDto getMacros() {
            return macros;
        }

        public void setMacros(MacroNutrientsDto macros) {
            this.macros = macros;
        }
    }

    public static class MacroPercentagesDto {
        private Double proteinPercentage;
        private Double carbohydratePercentage;
        private Double fatPercentage;

        public MacroPercentagesDto() {}

        public MacroPercentagesDto(Double proteinPercentage, Double carbohydratePercentage, Double fatPercentage) {
            this.proteinPercentage = proteinPercentage;
            this.carbohydratePercentage = carbohydratePercentage;
            this.fatPercentage = fatPercentage;
        }

        public Double getProteinPercentage() {
            return proteinPercentage;
        }

        public void setProteinPercentage(Double proteinPercentage) {
            this.proteinPercentage = proteinPercentage;
        }

        public Double getCarbohydratePercentage() {
            return carbohydratePercentage;
        }

        public void setCarbohydratePercentage(Double carbohydratePercentage) {
            this.carbohydratePercentage = carbohydratePercentage;
        }

        public Double getFatPercentage() {
            return fatPercentage;
        }

        public void setFatPercentage(Double fatPercentage) {
            this.fatPercentage = fatPercentage;
        }
    }
} 
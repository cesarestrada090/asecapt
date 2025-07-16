package com.fitech.app.trainers.application.dto;

import java.util.List;

public class MacroCalculationRequestDto {
    private List<SelectedFoodDto> selectedFoods;

    public MacroCalculationRequestDto() {}

    public MacroCalculationRequestDto(List<SelectedFoodDto> selectedFoods) {
        this.selectedFoods = selectedFoods;
    }

    public List<SelectedFoodDto> getSelectedFoods() {
        return selectedFoods;
    }

    public void setSelectedFoods(List<SelectedFoodDto> selectedFoods) {
        this.selectedFoods = selectedFoods;
    }

    public static class SelectedFoodDto {
        private Long foodId;
        private Double quantity;

        public SelectedFoodDto() {}

        public SelectedFoodDto(Long foodId, Double quantity) {
            this.foodId = foodId;
            this.quantity = quantity;
        }

        public Long getFoodId() {
            return foodId;
        }

        public void setFoodId(Long foodId) {
            this.foodId = foodId;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }
    }
} 
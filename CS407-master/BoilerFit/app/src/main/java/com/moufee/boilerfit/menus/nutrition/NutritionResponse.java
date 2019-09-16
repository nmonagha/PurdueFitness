package com.moufee.boilerfit.menus.nutrition;

import java.util.List;

public class NutritionResponse {
    private List<NutritionItem> nutrition;
    private String ID;
    private String ingredients;
    private String name;
    private boolean isVegetarian;


    public List<NutritionItem> getNutrition() {
        return nutrition;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public String getID() {
        return ID;
    }

    public class NutritionItem {
        private String name;
        private double value;
        private String labelValue;
        private String dailyValue;
        private int ordinal;

        public String getName() {
            return name;
        }

        public double getValue() {
            return value;
        }

        public String getLabelValue() {
            return labelValue;
        }

        public String getDailyValue() {
            return dailyValue;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }
}

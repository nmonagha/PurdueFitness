package com.moufee.boilerfit.menus;

import java.util.Set;

public class Allergens {
    private Set<String> allergenSet;

    public static String[] possibleAllergens = {"Eggs", "Fish", "Gluten", "Milk", "Peanuts", "Shellfish", "Soy", "Tree Nuts", "Wheat"};
    public static final String eggs = "Eggs";
    public static final String fish = "Fish";
    public static final String gluten = "Gluten";
    public static final String milk = "Milk";
    public static final String peanuts = "Peanuts";
    public static final String shellfish = "Shellfish";
    public static final String soy = "Soy";
    public static final String treeNuts = "Tree Nuts";
    public static final String vegan = "Vegan";
    public static final String vegetarian = "Vegetarian";
    public static final String wheat = "Wheat";

    public boolean hasEggs() {
        return allergenSet.contains(eggs);
    }

    public boolean hasFish() {
        return allergenSet.contains(fish);
    }

    public boolean hasGluten() {
        return allergenSet.contains(gluten);
    }

    public boolean hasMilk() {
        return allergenSet.contains(milk);
    }

    public boolean hasPeanuts() {
        return allergenSet.contains(peanuts);
    }

    public boolean hasShellfish() {
        return allergenSet.contains(shellfish);
    }

    public boolean hasSoy() {
        return allergenSet.contains(soy);
    }

    public boolean hasTreeNuts() {
        return allergenSet.contains(treeNuts);
    }

    public boolean isVegetarian() {
        return allergenSet.contains(vegetarian);
    }

    public boolean isVegan() {
        return allergenSet.contains(vegan);
    }

    public boolean hasWheat() {
        return allergenSet.contains(wheat);
    }

    public Allergens(Set<String> allergenSet) {
        this.allergenSet = allergenSet;
    }

    public boolean containsAllergen(String allergen) {
        return allergenSet.contains(allergen);
    }

    public Set<String> getAllergenSet() {
        return allergenSet;
    }

    @Override
    public String toString() {
        return allergenSet.toString();
    }
}

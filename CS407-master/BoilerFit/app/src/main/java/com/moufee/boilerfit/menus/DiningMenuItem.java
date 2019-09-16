package com.moufee.boilerfit.menus;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben on 9/5/17.
 * Represent one item on the menu
 */
@Keep
public class DiningMenuItem {
    @SerializedName("ID")
    private String id;
    private String name;
    private boolean isVegetarian;
    private Allergens allergens;

    public DiningMenuItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public String getId() {
        return id;
    }

    public Allergens getAllergens() {
        return allergens;
    }

    public boolean containsAllergen(String allergen) {
        return allergens.containsAllergen(allergen);
    }

    public boolean isVegan() {
        if (allergens == null) {
            return false;
        }
        return allergens.containsAllergen(Allergens.vegan);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DiningMenuItem && ((DiningMenuItem) obj).id.equals(this.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " :" + name + "" + allergens;
    }
}

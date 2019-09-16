package com.moufee.boilerfit.menus;

import android.support.annotation.Keep;

import org.joda.time.LocalTime;

import java.util.ArrayList;

/**
 * Mirrors JSON structure from API to allow easy deserialization
 * Contains inner classes to represent nested objects
 * Gson Constructs objects from JSON using these classes
 */
@Keep
public class DiningCourtMenu {

    private String location;
    private String date;
    private String notes;
    private ArrayList<Meal> meals;

    public String getLocation() {
        if (location != null)
            return location;
        return "NULL";
    }

    public String getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    /**
     * Gets the meal associated with the provided index
     * Index 3 is dinner, but some dining courts only have three meals (up to index 2)
     *
     * @param mealIndex the index of the meal to get
     * @return the Meal associated with the provided index
     */
    public Meal getMeal(int mealIndex) {
        if (meals != null && meals.size() > 0) {
            for (Meal meal :
                    meals) {
                if (meal.getOrder() == mealIndex + 1)
                    return meal;
            }
        }

        return null;
    }

    public boolean servesLateLunch() {
        if (meals != null)
            for (Meal meal :
                    meals) {
                if (meal.getName().equals("Late Lunch") && meal.isOpen())
                    return true;
            }
        return false;
//        return meals != null && meals.size() == 4 && meals.get(2).isOpen();
    }

    public boolean isServing(int mealIndex) {
        return meals != null && getMeal(mealIndex) != null && getMeal(mealIndex).isOpen();
    }

    @Keep
    public class Meal {

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }

        public String getStatus() {
            return status;
        }

        public Hours getHours() {
            return hours;
        }

        public ArrayList<Station> getStations() {
            return stations;
        }

        public boolean isOpen() {
            return status != null && status.equals("Open");
        }

        private String id;
        private String name;
        private int order;
        private String status;
        private Hours hours;
        private ArrayList<Station> stations;
    }

    @Keep
    public class Hours {

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        private LocalTime startTime;
        private LocalTime endTime;
    }


    @Keep
    public class Station {


        public String getName() {
            return name;
        }

        public ArrayList<DiningMenuItem> getItems() {
            return items;
        }

        public int getNumItems() {
            if (items == null)
                return 0;
            return items.size();
        }

        public DiningMenuItem getItem(int index) {
            return items.get(index);
        }

        private String name;
        private ArrayList<DiningMenuItem> items;

    }

}
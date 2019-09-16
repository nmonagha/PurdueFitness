package com.moufee.boilerfit;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private String uuid;
    private String email;
    private String bio;
    private Boolean isVegetarian = false;
    private Boolean isVegan = false;
    public Map<String, Boolean> allergies = new HashMap<>();
    private Map<String, Integer> diningCourtCounts = new HashMap<>();
    private Map<String, Boolean> blacklistedItems = new HashMap<>();
    private int foodStreak;
    private int foodStreakLevel;
    public List<String> favoriteActivities;

    private Map<String, String> friendRequestSent;
    private Map<String, String> friendRequestRecieved;

    public void setFriendRequestSent(Map<String, String> friendRequestSent) {
        this.friendRequestSent = friendRequestSent;
    }

    public void setFriendRequestRecieved(Map<String, String> friendRequestRecieved) {
        this.friendRequestRecieved = friendRequestRecieved;
    }
    public String getActivityStreakDate() {
        return activityStreakDate;
    }

    public void setActivityStreakDate(String activityStreakDate) {
        this.activityStreakDate = activityStreakDate;
    }

    private String activityStreakDate;

    private int activityStreak;

    public int getActivityStreak() {
        return activityStreak;
    }

    public void setActivityStreak(int activityStreak) {
        this.activityStreak = activityStreak;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    private int activityLevel;

    public String getFoodStreakDate() {
        return foodStreakDate;
    }

    public void setFoodStreakDate(String foodStreakDate) {
        this.foodStreakDate = foodStreakDate;
    }

    private String foodStreakDate;
    public int getFoodStreak(){
        return foodStreak;
    }

    public int getFoodStreakLevel(){
        return foodStreakLevel;
    }

    public int getNextFoodStreakLevel(){
        return (int) Math.pow(2,foodStreakLevel)*7;
    }

    public int getNextActivityStreakLevel(){
        return (int) Math.pow(2,activityLevel)*7;
    }
    private int[] getDateFromString(String dateString){
        String[] dateStringArr = dateString.split("/");
        int dateArr[] = new int[3];
        dateArr[0] = Integer.parseInt(dateStringArr[0]);
        dateArr[1] = Integer.parseInt(dateStringArr[1]);
        dateArr[2] = Integer.parseInt(dateStringArr[2]);
        return  dateArr;
    }

    private int[] getCurrentDate(){
        Calendar currentDate = Calendar.getInstance();
        int currentDateDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentDateMonth = currentDate.get(Calendar.MONTH);
        int currentDateYear = currentDate.get(Calendar.YEAR);
        int dateArr[] = new int[3];
        dateArr[0] = currentDateDay;
        dateArr[1] = currentDateMonth;
        dateArr[2] = currentDateYear;
        return  dateArr;
    }

    private String getStringFromDate(int[] date){
        return date[0] + "/" + date[1]+ "/" + date[2];
    }

    //0 - same day, 1 - streak increment, 2 - streak broke
    public int checkStreakUpdate(int[] streakDate){
        int[] currentDate = getCurrentDate();
        if (currentDate[0] == streakDate[0] && currentDate[1] == streakDate[1] && currentDate[2] == streakDate[2]){
           return 0;
        }
        else if ((currentDate[0] -1) == streakDate[0] && (currentDate[1]) == streakDate[1] && (currentDate[2]) == streakDate[2]){
            return 1;
        }
        return 2;
    }

    public void updateFoodStreak(){
        if(foodStreakDate == null){
            foodStreak = 1;
            foodStreakLevel = 0;
            this.foodStreakDate = getStringFromDate(getCurrentDate());
            return;
        }
        int checkStreak = checkStreakUpdate(getDateFromString(foodStreakDate));
        if(checkStreak == 1){
            foodStreak += 1;
            foodStreakDate = getStringFromDate(getCurrentDate());
            if(foodStreak >= getNextFoodStreakLevel()){
                foodStreakLevel+=1;
            }
        }else if(checkStreak == 2){
            foodStreak = 1;
            this.foodStreakDate = getStringFromDate(getCurrentDate());
        }
    }
    public void updateActivityStreak(){
        if(activityStreakDate == null){
            activityStreak = 1;
            activityLevel = 0;
            this.activityStreakDate = getStringFromDate(getCurrentDate());
            return;
        }
        int checkStreak = checkStreakUpdate(getDateFromString(activityStreakDate));
        if(checkStreak == 1){
            activityStreak += 1;
            activityStreakDate = getStringFromDate(getCurrentDate());
            if(activityStreak >= getNextActivityStreakLevel()){
                activityLevel+=1;
            }
        }else if(checkStreak == 2){
            activityStreak = 1;
            this.activityStreakDate = getStringFromDate(getCurrentDate());
            return;
        }
    }
    private int hoursForStreak(String streakDateString){
        if(streakDateString == null)
            return 24;
        int[] streakDate = getDateFromString(streakDateString);
        int checkStreak = checkStreakUpdate(streakDate);
        if(checkStreak == 2)
            return -1;
        if(checkStreak == 1){
            int hoursToMidnight = 24 - Calendar.getInstance().get(Calendar.HOUR);
            return hoursToMidnight;
        }
        return 24;
    }

    public int hoursForFoodStreak(){
        return hoursForStreak(foodStreakDate);
    }
    public int hoursForActivityStreak(){
        return hoursForStreak(activityStreakDate);
    }


    public Map<String, String> getFriends() {
        if(friends == null)
            return new HashMap<String, String>();
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    private Map<String,String> friends;

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public Map<String, Boolean> getAllergies() {
        return allergies;
    }

    public Map<String, Integer> getDiningCourtCounts() {
        if (diningCourtCounts == null) {
            diningCourtCounts = new HashMap<>();
        }
        return diningCourtCounts;
    }

    public Map<String, Boolean> getBlacklistedItems() {
        if (blacklistedItems == null) {
            blacklistedItems = new HashMap<>();
        }
        return blacklistedItems;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAllergies(Map<String, Boolean> allergies) {
        this.allergies = allergies;
    }

    public void setDiningCourtCounts(Map<String, Integer> diningCourtCounts) {
        this.diningCourtCounts = diningCourtCounts;
    }

    public boolean mustHaveVegetarian() {
        return isVegetarian;
    }

    public boolean mustHaveVegan() {
        return isVegan;
    }

    public void setVegetarian(Boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public void setVegan(Boolean vegan) {
        isVegan = vegan;
    }

    public boolean isAllergicTo(String allergen) {
        return allergies != null && allergies.containsKey(allergen) && allergies.get(allergen);
    }

    public void setBlacklistedItems(Map<String, Boolean> blacklistedItems) {
        this.blacklistedItems = blacklistedItems;
    }

    public boolean hasSentFriendRequest(String userID){
        if(friendRequestSent == null)
            return false;
        return friendRequestSent.containsKey(userID);
    }

    public boolean hasRecievedFriendRequest(String userID){
        if(friendRequestRecieved == null)
            return false;

        return friendRequestRecieved.containsKey(userID);
    }


    public Map<String, String> getFriendRequestRecieved(){
        if(friendRequestRecieved == null)
            return new HashMap<String, String>();
        return friendRequestRecieved;
    }

    public Map<String, String> getFriendRequestSent(){
        if(friendRequestSent == null)
            return new HashMap<String, String>();
        return friendRequestSent;
    }

    public void addFriendRequestRecieved(String userID){
        friendRequestRecieved.put(userID, userID);
    }

    public void deleteFriendRequestRecieved(String userID){
        if(hasRecievedFriendRequest(userID))
            friendRequestRecieved.remove(userID);
    }

    public void addFriendRequestSent(String userID){
        friendRequestRecieved.put(userID, userID);
    }

    public void deleteFriendRequestSent(String userID){
            if (hasSentFriendRequest(userID))
                friendRequestSent.remove(userID);
    }
    public void setFoodStreak(int foodStreak){
        this.foodStreak = foodStreak;
    }
    public List<String> getFavoriteActivities() {
        if (favoriteActivities == null) {
            favoriteActivities = new ArrayList<>();
        }
        return favoriteActivities;
    }

    public void setFoodStreakLevel(int foodStreakLevel){
        this.foodStreakLevel = foodStreakLevel;
    }
    public void setFavoriteActivities(List<String> favoriteActivities) {
        this.favoriteActivities = favoriteActivities;
    }
}

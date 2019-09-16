package com.moufee.boilerfit.ui.corec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoRecHelper {
    public static Map<String, List<String>> activitiesMap;

    public static Map<String, List<String>> getActivitiesMap() {
        if (activitiesMap != null)
            return activitiesMap;

        activitiesMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("Lower Gym 1");
        list.add("Lower Gym 2");
        list.add("Lower Gym 3");
        list.add("Upper Gym 1");
        list.add("Upper Gym 2");
        list.add("Upper Gym 3");
        activitiesMap.put("Badminton", list);

        list = new ArrayList<>();
        list.add("Lower Gym 1");
        list.add("Lower Gym 2");
        list.add("Lower Gym 3");
        list.add("Feature Gym");
        list.add("Upper Gym 1");
        list.add("Upper Gym 2");
        list.add("Upper Gym 3");
        list.add("Gold & Black Gyms 4-6");
        activitiesMap.put("Volleyball", list);

        list = new ArrayList<>();
        list.add("Feature Gym");
        list.add("Gold & Black Gyms 4-6");
        list.add("Upper Gym 1");
        list.add("Upper Gym 2");
        list.add("Upper Gym 3");
        activitiesMap.put("Basketball", list);


        list = new ArrayList<>();
        list.add("Racquet Ct 09");
        list.add("Racquet Ct 10");
        list.add("Racquet Ct 11");
        list.add("Racquet Ct 12");
        list.add("Racquet Ct 13");
        list.add("Racquet Ct 14");
        list.add("Racquet Ct 15");
        list.add("Racquet Ct 16");
        activitiesMap.put("Racquetball", list);

        list = new ArrayList<>();
        list.add("Bouldering Wall");
        list.add("Climbing Wall");
        activitiesMap.put("Climbing", list);

        list = new ArrayList<>();
        list.add("MAC");
        activitiesMap.put("Soccer", list);

        list = new ArrayList<>();
        list.add("MP5 Small");
        activitiesMap.put("Table Tennis", list);

        list = new ArrayList<>();
        list.add("MP3");
        list.add("MP4");
        activitiesMap.put("Wrestling", list);

        list = new ArrayList<>();
        list.add("MP1");
        list.add("MP2");
        list.add("MP5 large");
        list.add("MP6");
        list.add("MP7");
        activitiesMap.put("Group Exercise", list);

        list = new ArrayList<>();
        list.add("Rec Pool Open Area");
        list.add("Rec Pool Lap Lanes");
        activitiesMap.put("Water Sports", list);

        list = new ArrayList<>();
        list.add("Upper Track");
        list.add("Atrium Track");
        activitiesMap.put("Jogging", list);
        return activitiesMap;

    }

}

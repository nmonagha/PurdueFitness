package com.moufee.boilerfit.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts Dining Courts into a fixed order based on their name
 */

public class DiningCourtComparator implements Comparator<DiningCourtMenu> {
    public static final List<String> diningCourts = new ArrayList<>(Arrays.asList("Earhart", "Ford", "Wiley", "Windsor", "Hillenbrand"));

    private List<String> sortOrder;

    public DiningCourtComparator() {
        sortOrder = diningCourts;
    }

    public DiningCourtComparator(List<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(DiningCourtMenu o1, DiningCourtMenu o2) {
        if (o1 == null || o2 == null)
            return 0;
        int index1 = sortOrder.indexOf(o1.getLocation());
        int index2 = sortOrder.indexOf(o2.getLocation());

        if (index1 < index2)
            return -1;
        if (index1 > index2)
            return 1;
        return 0;
    }
}

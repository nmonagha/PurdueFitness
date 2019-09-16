package com.moufee.boilerfit.menus;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Contains all the menus for all campus dining courts for one day
 */

public class FullDayMenu {
    private List<DiningCourtMenu> mMenus;
    private DateTime mDate;
    private boolean mLateLunchServed;

    public FullDayMenu(List<DiningCourtMenu> diningCourtMenus, DateTime date, boolean lateLunchServed) {
        mMenus = diningCourtMenus;
        mDate = date;
        mLateLunchServed = lateLunchServed;


    }

    public int getNumMenus() {
        return mMenus.size();
    }



    public List<DiningCourtMenu> getMenus() {
        return mMenus;
    }

    /**
     * Gets a  menu from a designated index
     *
     * @param index, the index of the {@link DiningCourtMenu} to get
     * @return a {@link DiningCourtMenu}, or null if none exists
     */
    public DiningCourtMenu getMenu(int index) {
        if (mMenus != null && index < mMenus.size())
            return mMenus.get(index);
        return null;
    }

    public boolean isLateLunchServed() {
        return mLateLunchServed;
    }

}

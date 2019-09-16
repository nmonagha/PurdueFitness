package com.moufee.boilerfit.corec;

public class CorecFacility {
    private String locationId;
    private String locationName;
    private String displayName;
    private String reservationTypeName;
    private int capacity;
    private int count;
    private Location location;

    @Override
    public String toString() {
        return String.format("%s : %s", locationName, displayName);
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getReservationTypeName() {
        return reservationTypeName;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCount() {
        return count;
    }

    public class Location {
        private String locationName;
        private String LocationId;
        private int capacity;
        private int sortOrder;
        private boolean active;
    }
}

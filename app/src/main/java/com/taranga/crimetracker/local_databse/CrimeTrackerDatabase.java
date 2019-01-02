package com.taranga.crimetracker.local_databse;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = CrimeTrackerDatabase.NAME, version = CrimeTrackerDatabase.VERSION)
public class CrimeTrackerDatabase {
    public static final String NAME = "CrimeTracker";
    public static final int VERSION = 3;
}

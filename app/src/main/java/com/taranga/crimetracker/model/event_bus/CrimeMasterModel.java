package com.taranga.crimetracker.model.event_bus;

import com.google.firebase.database.IgnoreExtraProperties;
import com.taranga.crimetracker.model.CrimeModel;

/**
 * Created by Niranjan on 17-Apr-18.
 */
@IgnoreExtraProperties
public class CrimeMasterModel {

    private  String key;
    private CrimeModel crimeModel;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CrimeModel getCrimeModel() {
        return crimeModel;
    }

    public void setCrimeModel(CrimeModel crimeModel) {
        this.crimeModel = crimeModel;
    }

    @Override
    public String toString() {
        return "CrimeMasterModel{" +
                "key='" + key + '\'' +
                ", crimeModel=" + crimeModel +
                '}';
    }
}

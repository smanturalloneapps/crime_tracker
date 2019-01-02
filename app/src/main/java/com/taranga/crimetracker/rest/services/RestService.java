package com.taranga.crimetracker.rest.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taranga.crimetracker.model.CrimeDetailsModel;

import java.util.List;

public class RestService {

    @SerializedName("Sheet1")
    @Expose
    private List<CrimeDetailsModel> crimeDetailsSheet;

    // Getter Methods


    public List<CrimeDetailsModel> getCrimeDetailsSheet() {
        return crimeDetailsSheet;
    }
}

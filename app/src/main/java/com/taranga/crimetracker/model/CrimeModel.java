package com.taranga.crimetracker.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by SHIVU on 4/7/2018.
 */
@IgnoreExtraProperties
public class CrimeModel {
    private Integer id;

    private String firebaseId;

    private String crime;

    private Double latitude;

    private Double longitude;

    private String date;


    private String time;

    private String address;


    public CrimeModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getCrime() {
        return crime;
    }

    public void setCrime(String crime) {
        this.crime = crime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CrimeModel{" +
                "id=" + id +
                ", firebaseId='" + firebaseId + '\'' +
                ", crime='" + crime + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}

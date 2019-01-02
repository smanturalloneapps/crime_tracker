package com.taranga.crimetracker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.taranga.crimetracker.local_databse.CrimeTrackerDatabase;

/**
 * Created by Niranjan on 20-Feb-18.
 */
@Table(database = CrimeTrackerDatabase.class)
public class CrimeDetailsModel extends BaseModel {

    @PrimaryKey
    @SerializedName("Id")
    @Expose
    private Integer id;
    @Column
    @SerializedName("FirebaseId")
    @Expose
    private String firebaseId;
    @Column
    @SerializedName("Crime")
    @Expose
    private String crime;
    @Column
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @Column
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @Column
    @SerializedName("Date")
    @Expose
    private String date;
    @Column
    @SerializedName("Time")
    @Expose
    private String time;
    @Column
    @SerializedName("Address")
    @Expose
    private String address;

    public CrimeDetailsModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrimeDetailsModel)) return false;

        CrimeDetailsModel that = (CrimeDetailsModel) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getFirebaseId() != null ? !getFirebaseId().equals(that.getFirebaseId()) : that.getFirebaseId() != null)
            return false;
        if (getCrime() != null ? !getCrime().equals(that.getCrime()) : that.getCrime() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(that.getLatitude()) : that.getLatitude() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(that.getLongitude()) : that.getLongitude() != null)
            return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null)
            return false;
        if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null)
            return false;
        return getAddress() != null ? getAddress().equals(that.getAddress()) : that.getAddress() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFirebaseId() != null ? getFirebaseId().hashCode() : 0);
        result = 31 * result + (getCrime() != null ? getCrime().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CrimeDetailsModel{" +
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



/*

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CrimeDetailsModel))
            return false;

        CrimeDetailsModel crimeDetailsModel = (CrimeDetailsModel) o;

        return this.getId() != null ? !this.getId().equals(crimeDetailsModel.getId()) : this.getId() == null
                && (this.getCrime() != null ? !this.getCrime().equals(crimeDetailsModel.getCrime()) : this.getCrime() == null
                && (this.getLatitude() != null ? !this.getLatitude().equals(crimeDetailsModel.getLatitude()) : this.getLatitude() == null
                && (this.getLongitude() != null ? !this.getLongitude().equals(crimeDetailsModel.getLongitude()) : this.getLongitude() == null
                && (this.getDate() != null ? !this.getDate().equals(crimeDetailsModel.getDate()) : this.getDate() == null
                && (this.getTime() != null ? !this.getTime().equals(crimeDetailsModel.getTime()) : this.getTime() == null
                && (this.getAddress() != null ? !this.getAddress().equals(crimeDetailsModel.getAddress()) : this.getAddress() == null))))));

*//*

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getCrime() != null ? !getCrime().equals(that.getCrime()) : that.getCrime() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(that.getLatitude()) : that.getLatitude() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(that.getLongitude()) : that.getLongitude() != null)
            return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null)
            return false;
        if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null)
            return false;
        return getAddress() != null ? getAddress().equals(that.getAddress()) : that.getAddress() == null;
*//*
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getCrime() != null ? getCrime().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CrimeDetailsModel{" +
                "id=" + id +
                ", crime='" + crime + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                '}';
    }*/

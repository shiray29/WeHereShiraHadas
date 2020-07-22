package com.example.wehere;

public class Profile {
    private String name, adress, id, cellnum, password, email, uriId;
    private boolean isOld, isBuild, isClean, isCompany, isShop, isCall;
    private double longitude, latitude;


    public String getUriId() {
        return uriId;
    }

    public Profile() {
        this.name = "";
        this.adress = "";
        this.id = "";
        this.cellnum = "";
        this.password = "";
        this.email = "";
        this.uriId = "";
        this.isOld = false;
        this.isBuild = false;
        this.isClean = false;
        this.isCompany = false;
        this.isShop = false;
        this.isCall = false;
        this.longitude = 0;
        this.latitude = 0;

    }


    public Profile(String name, String adress, String id, String cellnum, String email, String password,
                   String uriId, boolean isOld, boolean isBuild, boolean isClean, boolean isCompany, boolean isShop, boolean isCall, Double longitude, Double latitude) {
        this.name = name;
        this.adress = adress;
        this.id = id;
        this.cellnum = cellnum;
        this.password = password;
        this.email = email;
        this.uriId = uriId;
        this.isOld = isOld;
        this.isBuild = isBuild;
        this.isClean = isClean;
        this.isCompany = isCompany;
        this.isShop = isShop;
        this.isCall = isCall;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCellnum() {
        return cellnum;
    }

    public void setCellnum(String cellnum) {
        this.cellnum = cellnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOld() {
        return isOld;
    }

    public void setOld(boolean old) {
        isOld = old;
    }

    public void setUriId(String uriId) {
        this.uriId = uriId;
    }

    public String getUriId(String uriId) {
        return uriId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(String password) {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsBuild() {
        return isBuild;
    }

    public void setIsBuild(boolean isBuild) {
        this.isBuild = isBuild;
    }

    public boolean getClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        isClean = clean;
    }

    public boolean getCompany() {
        return isCompany;
    }

    public void setCompany(boolean company) {
        isCompany = company;
    }

    public boolean getShop() {
        return isShop;
    }

    public void setShop(boolean shop) {
        isShop = shop;
    }

    public boolean getCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }
}
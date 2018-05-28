package com.byteshaft.abnlookup;

import java.io.Serializable;
import java.util.ArrayList;

public class NameDetailSerializer implements Serializable{

    private String entityName;
    private String abnStatus;
    private String entityType;
    private String gst;
    private String businessLocation;
    private String tradingName;
    private String lastUpdated;
    private ArrayList<BusinessName> businessNames;
    private String ACN;

    public String getACN() {
        return ACN;
    }

    public void setACN(String ACN) {
        this.ACN = ACN;
    }

    public ArrayList<BusinessName> getBusinessNames() {
        return businessNames;
    }

    public void setBusinessNames(ArrayList<BusinessName> businessNames) {
        this.businessNames = businessNames;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAbnStatus() {
        return abnStatus;
    }

    public void setAbnStatus(String abnStatus) {
        this.abnStatus = abnStatus;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }

    public String getTradingName() {
        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

package com.byteshaft.abnlookup;

import java.io.Serializable;

public class Serializer implements Serializable {

    // Physical Addresses//

    private String effectiveTo;
    private String postcode;
    private String stateCode;
    private String effectiveFrom;
    private String ABN;

    public boolean isCurrentIndicator() {
        return isCurrentIndicator;
    }

    public void setCurrentIndicator(boolean currentIndicator) {
        isCurrentIndicator = currentIndicator;
    }

    private boolean isCurrentIndicator;

    public boolean isAbnActive() {
        return abnActive;
    }

    public void setAbnActive(boolean abnActive) {
        this.abnActive = abnActive;
    }

    public String getAbnFrom() {
        return abnFrom;
    }

    public void setAbnFrom(String abnFrom) {
        this.abnFrom = abnFrom;
    }

    private boolean abnActive;
    private String abnFrom;

    public String getABN() {
        return ABN;
    }

    public void setABN(String ABN) {
        this.ABN = ABN;
    }

    public String getACN() {
        return ACN;
    }

    public void setACN(String ACN) {
        this.ACN = ACN;
    }

    private String ACN;


    public String getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(String effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /// ------------------------ ///

    // Main Name

    String organisationName;
    String mainNameEffectiveFrom;
    String mainNameEffectiveTo;

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getMainNameEffectiveFrom() {
        return mainNameEffectiveFrom;
    }

    public void setMainNameEffectiveFrom(String mainNameEffectiveFrom) {
        this.mainNameEffectiveFrom = mainNameEffectiveFrom;
    }

    public String getMainNameEffectiveTo() {
        return mainNameEffectiveTo;
    }

    public void setMainNameEffectiveTo(String mainNameEffectiveTo) {
        this.mainNameEffectiveTo = mainNameEffectiveTo;
    }

    ///----------------------////

    //Entity Status;
    String entityStatus;

    public String getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(String entityStatus) {
        this.entityStatus = entityStatus;
    }

    ///----------------////



    //  identifierValue;

    String identifierValue;

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

}
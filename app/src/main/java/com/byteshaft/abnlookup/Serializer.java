package com.byteshaft.abnlookup;

import java.io.Serializable;

public class Serializer implements Serializable {

    // Physical Addresses//

    String effectiveTo;
    String postcode;
    String stateCode;
    String effectiveFrom;

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
package com.example.uichart.ui.json;

class Descriptions
{

    public String en;
    public String ja;

}


public class JSONGeneralLighing {

    public String businessFacilityCode;
    public String faultDescription;
    public Boolean faultStatus;
    public String installationLocation;
    public Manufacturer manufacturer;
    public String manufacturerFaultCode;
    public MaximumSpecifiableLevel maximumSpecifiableLevel;
    public Boolean operationStatus;
    public Boolean powerSaving;
    public String productCode;
    public String productionDate;
    public Protocol protocol;
    public String serialNumber;

}



class Manufacturer {

    public String code;
    public Descriptions descriptions;

}


class MaximumSpecifiableLevel {

    public Integer brightness;
    public String color;

}

class Protocol {

    public String type;
    public String version;

}
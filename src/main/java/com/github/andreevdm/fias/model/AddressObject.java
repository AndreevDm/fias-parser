package com.github.andreevdm.fias.model;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public class AddressObject {
    private final String guid;
    private final int regionId;

    private String formalName;
    private String officialName;
    private String shortName;


    public AddressObject(String guid, int regionId) {
        this.guid = guid;
        this.regionId = regionId;
    }

    public String getType(){
        return shortName;
    }

    public String getGuid() {
        return guid;
    }

    public int getRegionId() {
        return regionId;
    }

    public String getFormalName() {
        return formalName;
    }

    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName(){
        return officialName != null ? officialName : formalName;
    }

    @Override
    public String toString() {
        return shortName + " " + getName();
    }
}

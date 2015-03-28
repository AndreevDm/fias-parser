package com.github.andreevdm.fias.model;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public class House {
    private final String guid;
    private final String addressObjectGuid;
    private AddressObject addressObject;


    private String number;
    private String building ;
    private String structure ;


    public House(String guid, String addressObjectGuid) {
        this.guid = guid;
        this.addressObjectGuid = addressObjectGuid;
    }

    public String getFullNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        if (number != null) {
            stringBuilder.append(number);
        }
        if (building != null) {
            stringBuilder.append("к").append(building);
        }
        if (structure != null) {
            stringBuilder.append(" стр").append(structure);
        }
        return stringBuilder.toString();
    }

    public String getNumber() {
        return number;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGuid() {
        return guid;
    }

    public String getAddressObjectGuid() {
        return addressObjectGuid;
    }

    public AddressObject getAddressObject() {
        return addressObject;
    }

    public void setAddressObject(AddressObject addressObject) {
        this.addressObject = addressObject;
    }

    @Override
    public String toString() {
        if (addressObject != null) {
            return addressObject.toString() + " " + getFullNumber();
        } else {
            return getFullNumber();
        }
    }
}

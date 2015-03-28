package com.github.andreevdm.fias.parser;

import com.github.andreevdm.fias.model.AddressObject;
import com.github.andreevdm.fias.model.House;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;
import java.util.Set;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public class HouseHandler extends FiasHandler<House> {

    private final Set<String> addressObjectsFilter;
    private final Map<String, AddressObject> addressObjects;

    public HouseHandler(Callback<House> callback, Set<String> addressObjectsFilter,
                        Map<String, AddressObject> addressObjects) {
        super("House", callback);
        this.addressObjectsFilter = addressObjectsFilter;
        this.addressObjects = addressObjects;
    }

    public HouseHandler(Callback<House> callback, Set<String> addressObjectsFilter) {
        this(callback, addressObjectsFilter, null);
    }

    public HouseHandler(Callback<House> callback, Map<String, AddressObject> addressObjects) {
        this(callback, null, addressObjects);
    }

    public HouseHandler(Callback<House> callback) {
        this(callback, null, null);
    }

    @Override
    protected House onElement() {
        String addressObjectGuid = get("AOGUID");
        if (addressObjectsFilter != null && !addressObjectsFilter.contains(addressObjectGuid)) {
            return null;
        }
        String guid = get("HOUSEGUID");
        House house = new House(guid, addressObjectGuid);
        if (addressObjects != null) {
            house.setAddressObject(addressObjects.get(addressObjectGuid));
        }
        house.setNumber(get("HOUSENUM"));
        house.setBuilding(get("BUILDNUM"));
        house.setStructure(get("STRUCNUM"));

        return house;
    }


}

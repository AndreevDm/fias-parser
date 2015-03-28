package com.github.andreevdm.fias.parser;

import com.github.andreevdm.fias.model.AddressObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public class AddressObjectHandler extends FiasHandler<AddressObject> {

    private final int regionCodeFilter;

    public AddressObjectHandler(Callback<AddressObject> callback, int regionCodeFilter) {
        super("Object", callback);
        this.regionCodeFilter = regionCodeFilter;
    }

    public AddressObjectHandler(Callback<AddressObject> callback) {
        this(callback, -1);
    }

    @Override
    protected AddressObject onElement() {
        int regionCode = getInt("REGIONCODE");
        if (regionCodeFilter > 0 && regionCodeFilter != regionCode) {
            return null;
        }
        String guid = get("AOGUID");
        AddressObject addressObject = new AddressObject(guid, regionCode);

        addressObject.setFormalName(get("FORMALNAME"));
        addressObject.setOfficialName(get("OFFNAME"));
        addressObject.setShortName(get("SHORTNAME"));
        return addressObject;
    }


}

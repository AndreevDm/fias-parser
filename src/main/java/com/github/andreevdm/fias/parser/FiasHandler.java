package com.github.andreevdm.fias.parser;

import com.github.andreevdm.fias.model.House;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public abstract class FiasHandler<T> extends DefaultHandler {

    private static final Logger log = LogManager.getLogger();

    private final String elementFilter;
    private final Callback<T> callback;
    private int total;
    private int filtered;
    private Attributes attrs;


    public FiasHandler(String elementFilter, Callback<T> callback) {
        this.elementFilter = elementFilter;
        this.callback = callback;
    }

    protected void incFiltered() {
        filtered++;
    }

    public int getTotal() {
        return total;
    }

    public int getFiltered() {
        return filtered;
    }

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        if (!qName.equals(elementFilter)) {
            return;
        }
        this.attrs = attrs;
//        debugAttrs();
        T element = onElement();

        if (element != null) {
            callback.onElement(element);
        } else {
            filtered++;
        }
        total++;
        if (total % 1000000 == 0) {
            logInfo();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        logInfo();
    }

    private void logInfo() {
        log.info("Processed " + total + " " + elementFilter + ", filtered: " + filtered);
    }

    private void debugAttrs() {
        System.out.println("----------------------");
        for (int i = 0; i < attrs.getLength(); i++) {
            System.out.println(attrs.getQName(i) + ": " + attrs.getValue(i));
        }
    }

    protected abstract T onElement();

    protected int getInt(String name) {
        return Integer.parseInt(attrs.getValue(name));
    }

    protected int getInt(String name, int defaultValue) {
        String value = attrs.getValue(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }


    protected String get(String name) {
        return attrs.getValue(name);
    }

    public static interface Callback<T> {
        void onElement(T element);
    }


}

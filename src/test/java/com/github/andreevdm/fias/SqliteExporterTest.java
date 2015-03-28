package com.github.andreevdm.fias;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class SqliteExporterTest {

    @Test
    public void testExport() throws Exception {

        System.setProperty("org.apache.logging.log4j.level", "INFO");
        String addrObjFile = "/Users/andreevdm/tmp/fias/fias_xml/AS_ADDROBJ_20150326_47dbf832-0c57-4bac-a1ee-bed6471c3bc4.XML";
        String houseFile = "/Users/andreevdm/tmp/fias/fias_xml/AS_HOUSE_20150326_e8f991d4-e8f1-4119-bc2f-8be54d7a1d67.XML";
        String outputFile = "/Users/andreevdm/tmp/fias/fias.db";
        SqliteExporter.export(addrObjFile, houseFile, 77, outputFile);
    }
}
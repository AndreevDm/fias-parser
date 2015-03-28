package com.github.andreevdm.fias;

import com.github.andreevdm.fias.model.AddressObject;
import com.github.andreevdm.fias.model.House;
import com.github.andreevdm.fias.parser.AddressObjectHandler;
import com.github.andreevdm.fias.parser.FiasHandler;
import com.github.andreevdm.fias.parser.HouseHandler;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dmitry Andreev <a href="mailto:AndreevDm@yandex-team.ru"/>
 * @date 28/03/15
 */
public class SqliteExporter {

    private static final Logger log = LogManager.getLogger();

    private static final int BATCH_SIZE = 50000;

    public static void export(String addressObjectFile, String houseFile, int regionCode,
                              String outputFile) throws Exception {
        final JdbcTemplate jdbcTemplate = createDatabase(outputFile);
        final Map<String, AddressObject> addressObjects = getAddressObjects(addressObjectFile, regionCode);
        saveAddressObjects(jdbcTemplate, new ArrayList<AddressObject>(addressObjects.values()));
        log.info("Saved " + addressObjects.size() + " address objects");

        final List<House> houses = new ArrayList<House>(BATCH_SIZE);

        final AtomicInteger count = new AtomicInteger();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new FileInputStream(houseFile), new HouseHandler(
            new FiasHandler.Callback<House>() {
                @Override
                public void onElement(House house) {
                    houses.add(house);
                    count.incrementAndGet();
                    if (houses.size() >= BATCH_SIZE) {
                        saveHouses(jdbcTemplate, houses);
                        log.info("Saved " + houses.size() + " houses, total saved " + count.get());
                        houses.clear();
                    }
                }
            },
            addressObjects.keySet(),
            addressObjects
        ));
        saveHouses(jdbcTemplate, houses);

        log.info("Parsed and saved " + count.get() + " houses");
        BasicDataSource dataSource = (BasicDataSource) jdbcTemplate.getDataSource();
        dataSource.close();
        log.info("File saved: " + outputFile);

    }

    private static Map<String, AddressObject> getAddressObjects(String file, int regionCode) throws Exception {

        final Map<String, AddressObject> addressObjects = new HashMap<String, AddressObject>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new FileInputStream(file), new AddressObjectHandler(
            new FiasHandler.Callback<AddressObject>() {
                @Override
                public void onElement(AddressObject addressObject) {
                    addressObjects.put(addressObject.getGuid(), addressObject);
                }
            },
            regionCode
        ));
        return addressObjects;
    }

    private static void saveAddressObjects(JdbcTemplate jdbcTemplate, final List<AddressObject> addressObjects) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO streets (id, type, street) VALUES (?, ?, ?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AddressObject addressObject = addressObjects.get(i);
                    ps.setString(1, addressObject.getGuid());
                    ps.setString(2, addressObject.getType());
                    ps.setString(3, addressObject.getName());
                }

                @Override
                public int getBatchSize() {
                    return addressObjects.size();
                }
            }
        );
    }

    private static void saveHouses(JdbcTemplate jdbcTemplate, final List<House> houses) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO houses (id, type, street, number) VALUES (?, ?, ?, ?)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    House house = houses.get(i);
                    AddressObject addressObject = house.getAddressObject();
                    ps.setString(1, house.getGuid());
                    ps.setString(2, addressObject.getType());
                    ps.setString(3, addressObject.getName());
                    ps.setString(4, house.getFullNumber());
                }

                @Override
                public int getBatchSize() {
                    return houses.size();
                }
            }
        );
    }

    private static JdbcTemplate createDatabase(String fileName) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:" + fileName);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        createDatabase(jdbcTemplate);
        return jdbcTemplate;
    }

    private static void createDatabase(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("DROP TABLE IF EXISTS houses");
        jdbcTemplate.update(
            "CREATE TABLE houses (" +
                "id TEXT NOT NULL, " +
                "type TEXT NOT NULL," +
                "street TEXT NOT NULL," +
                "number TEXT NOT NULL)"
        );
        jdbcTemplate.update("DROP TABLE IF EXISTS streets");
        jdbcTemplate.update(
            "CREATE TABLE streets (" +
                "id TEXT NOT NULL, " +
                "type TEXT NOT NULL," +
                "street TEXT NOT NULL)"
        );
    }

}

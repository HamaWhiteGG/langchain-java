package com.hw.langchain.sql.database;

import java.util.Map;

/**
 * TODO TO Java
 *
 * @description: SQLAlchemy wrapper around a database.
 * @author: HamaWhite
 */
public class SQLDatabase {

    public static SQLDatabase fromURI(String databaseURI, Map<String, Object> optionMap) {
        return new SQLDatabase();
    }

    public String getDialect() {
        return null;
    }
}

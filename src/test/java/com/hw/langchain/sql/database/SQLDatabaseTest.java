package com.hw.langchain.sql.database;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;

/**
 * @description: SQLDatabaseTest
 * @author: HamaWhite
 */
public class SQLDatabaseTest {

    private static SQLDatabase database;

    @BeforeAll
    public static void setup() throws SQLException {
        database = new SQLDatabase("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "demo", "demo");
    }


    @AfterAll
    public static void cleanup() throws SQLException {
        database.close();
    }

}
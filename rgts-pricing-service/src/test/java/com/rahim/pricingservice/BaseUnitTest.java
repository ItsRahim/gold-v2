package com.rahim.pricingservice;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@SpringBootTest
@TestPropertySource("classpath:application.yml")
public abstract class BaseUnitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearDatabase() {
        String schema = "rgts";
        String[] tables = {"gold_types"};

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE " + schema + "." + table);
        }
    }
}

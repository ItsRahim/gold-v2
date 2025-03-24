package com.rahim.pricingservice;

import com.rahim.common.exception.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(ApiExceptionHandler.class)
@TestPropertySource("classpath:application.yml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void clearDatabase() {
        String schema = "rgts";
        String[] tables = {"gold_types", "gold_prices"};

        for (String table : tables) {
            jdbcTemplate.execute("TRUNCATE TABLE IF EXISTS" + schema + "." + table);
        }
    }
}

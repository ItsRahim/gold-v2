package com.rahim.pricingservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rahim Ahmed
 * @created 24/03/2025
 */
@Transactional
@SpringBootTest
@TestPropertySource("classpath:application.yml")
public abstract class BaseUnitTest {
}

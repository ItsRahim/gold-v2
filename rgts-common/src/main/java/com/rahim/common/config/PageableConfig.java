package com.rahim.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * @created 05/04/2025
 * @author Rahim Ahmed
 */
@Configuration
@EnableSpringDataWebSupport(
    pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PageableConfig {}

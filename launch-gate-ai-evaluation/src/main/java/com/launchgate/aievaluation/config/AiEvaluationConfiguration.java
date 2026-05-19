package com.launchgate.aievaluation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация ИИ модуля.
 */
@Configuration
@EnableConfigurationProperties(AiEvaluationProperties.class)
public class AiEvaluationConfiguration {
}

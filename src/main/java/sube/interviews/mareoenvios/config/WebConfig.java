package sube.interviews.mareoenvios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Configuración web para soporte de Spring Data.
 * 
 * Habilita soporte para Pageable y serialización de Page<T>.
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfig {
}
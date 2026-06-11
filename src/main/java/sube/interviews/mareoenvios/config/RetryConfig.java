package sube.interviews.mareoenvios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Habilita el soporte de @Retryable en el contexto de Spring.
 * Los métodos anotados con @Retryable se reintentarán hasta 3 veces
 * con backoff de 500ms en caso de excepción.
 */
@Configuration
@EnableRetry
public class RetryConfig {
}

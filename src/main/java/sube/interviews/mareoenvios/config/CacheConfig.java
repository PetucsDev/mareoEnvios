package sube.interviews.mareoenvios.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * Configuración de caché para el perfil de producción (Redis).
 *
 * En el perfil "test" esta clase no se carga (@Profile("!test")).
 * Spring Boot auto-configura un SimpleCacheManager gracias a
 * spring.cache.type=simple en src/test/resources/application.properties,
 * lo que evita la necesidad de Redis durante los tests.
 */
@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configurar ObjectMapper con type information Y soporte para fechas
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Habilitar type information para preservar genéricos
        objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(), 
            ObjectMapper.DefaultTyping.NON_FINAL, 
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        
        // Crear serializer con ObjectMapper configurado
        GenericJackson2JsonRedisSerializer jsonSerializer = 
            new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "customers",          defaultConfig.entryTtl(Duration.ofMinutes(15)),
                "customers-all",      defaultConfig.entryTtl(Duration.ofMinutes(15)),
                "shipping",           defaultConfig.entryTtl(Duration.ofMinutes(10)),
                "shippings-by-state", defaultConfig.entryTtl(Duration.ofMinutes(5)),
                "shippings-by-date",  defaultConfig.entryTtl(Duration.ofMinutes(5)),
                "top-products",       defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}

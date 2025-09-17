package co.za.vendingmachineapi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
    private final CacheConfigProperties cacheConfigProperties;

    @Bean
    @Qualifier("myCacheManager")
    public CacheManager cacheManager() {
        final CaffeineCacheManager manager = new CaffeineCacheManager();
        cacheConfigProperties.getCaffeineCaches()
                .forEach(cc -> createCaffeineCache(manager, cc));
        return manager;
    }

    private void createCaffeineCache(CaffeineCacheManager manager, CacheConfigProperties.CaffeineCache caffeineCache) {
        Cache<Object, Object> cache = Caffeine.from(caffeineCache.getSpec()).build();
        caffeineCache.getCacheNames()
                .forEach(cn -> manager.registerCustomCache(cn, cache));
    }
}


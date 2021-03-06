package com.nepxion.discovery.plugin.strategy.service.configuration;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nepxion.discovery.common.exception.DiscoveryException;
import com.nepxion.discovery.plugin.strategy.adapter.DiscoveryEnabledAdapter;
import com.nepxion.discovery.plugin.strategy.constant.StrategyConstant;
import com.nepxion.discovery.plugin.strategy.service.adapter.DefaultDiscoveryEnabledAdapter;
import com.nepxion.discovery.plugin.strategy.service.aop.FeignStrategyInterceptor;
import com.nepxion.discovery.plugin.strategy.service.aop.RestTemplateStrategyInterceptor;
import com.nepxion.discovery.plugin.strategy.service.aop.ServiceStrategyAutoScanProxy;
import com.nepxion.discovery.plugin.strategy.service.aop.ServiceStrategyInterceptor;
import com.nepxion.discovery.plugin.strategy.service.constant.ServiceStrategyConstant;

@Configuration
@AutoConfigureBefore(RibbonClientConfiguration.class)
@ConditionalOnProperty(value = StrategyConstant.SPRING_APPLICATION_STRATEGY_CONTROL_ENABLED, matchIfMissing = true)
public class ServiceStrategyAutoConfiguration {
    @Value("${" + ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES + ":}")
    private String scanPackages;

    @Value("${" + ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_REQUEST_HEADERS + ":}")
    private String requestHeaders;

    @Bean
    @ConditionalOnProperty(value = ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES, matchIfMissing = false)
    public ServiceStrategyAutoScanProxy serviceStrategyAutoScanProxy() {
        if (StringUtils.isEmpty(scanPackages)) {
            throw new DiscoveryException(ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES + "'s value can't be empty, remove it if useless");
        }

        if (ServiceStrategyConstant.EXCLUSION_SCAN_PACKAGES.contains(scanPackages)) {
            throw new DiscoveryException("It can't scan packages for '" + ServiceStrategyConstant.EXCLUSION_SCAN_PACKAGES + "', please check '" + ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES + "'");
        }

        return new ServiceStrategyAutoScanProxy(scanPackages);
    }

    @Bean
    @ConditionalOnProperty(value = ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES, matchIfMissing = false)
    public ServiceStrategyInterceptor serviceStrategyInterceptor() {
        if (StringUtils.isEmpty(scanPackages)) {
            throw new DiscoveryException(ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES + " can't be empty, remove it if useless");
        }

        if (ServiceStrategyConstant.EXCLUSION_SCAN_PACKAGES.contains(scanPackages)) {
            throw new DiscoveryException("It can't scan packages for '" + ServiceStrategyConstant.EXCLUSION_SCAN_PACKAGES + "', please check '" + ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_SCAN_PACKAGES + "'");
        }

        return new ServiceStrategyInterceptor();
    }

    @Bean
    @ConditionalOnProperty(value = ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_REQUEST_HEADERS, matchIfMissing = false)
    public FeignStrategyInterceptor feignStrategyInterceptor() {
        if (StringUtils.isEmpty(requestHeaders)) {
            throw new DiscoveryException(ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_REQUEST_HEADERS + " can't be empty, remove it if useless");
        }

        return new FeignStrategyInterceptor(requestHeaders);
    }

    @Bean
    @ConditionalOnProperty(value = ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_REQUEST_HEADERS, matchIfMissing = false)
    public RestTemplateStrategyInterceptor restTemplateStrategyInterceptor() {
        if (StringUtils.isEmpty(requestHeaders)) {
            throw new DiscoveryException(ServiceStrategyConstant.SPRING_APPLICATION_STRATEGY_REQUEST_HEADERS + " can't be empty, remove it if useless");
        }

        return new RestTemplateStrategyInterceptor(requestHeaders);
    }

    @Bean
    @ConditionalOnMissingBean
    public DiscoveryEnabledAdapter discoveryEnabledAdapter() {
        return new DefaultDiscoveryEnabledAdapter();
    }
}
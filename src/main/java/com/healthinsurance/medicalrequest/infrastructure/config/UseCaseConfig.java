package com.healthinsurance.medicalrequest.infrastructure.config;

import com.healthinsurance.medicalrequest.application.port.in.*;
import com.healthinsurance.medicalrequest.application.port.out.*;
import com.healthinsurance.medicalrequest.application.usecase.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Wires use case beans with transactional proxies.
 * Use cases are framework-free, so we wrap them in AOP proxies to provide
 * transaction boundaries. This ensures atomicity and proper event publishing.
 */
@Configuration
@EnableTransactionManagement
public class UseCaseConfig {

    @Bean
    public CreateRequestUseCase createRequestUseCase(MedicalRequestRepository repository,
                                                      CoverageCheckPort coverageCheckPort,
                                                      PlatformTransactionManager txManager) {
        CreateRequestUseCaseImpl target = new CreateRequestUseCaseImpl(repository, coverageCheckPort);
        return createTransactionalProxy(target, CreateRequestUseCase.class, txManager);
    }

    @Bean
    public SubmitRequestUseCase submitRequestUseCase(MedicalRequestRepository repository,
                                                      EventPublisherPort eventPublisher,
                                                      PlatformTransactionManager txManager) {
        SubmitRequestUseCaseImpl target = new SubmitRequestUseCaseImpl(repository, eventPublisher);
        return createTransactionalProxy(target, SubmitRequestUseCase.class, txManager);
    }

    @Bean
    public ApproveRequestUseCase approveRequestUseCase(MedicalRequestRepository repository,
                                                        EventPublisherPort eventPublisher,
                                                        PlatformTransactionManager txManager) {
        ApproveRequestUseCaseImpl target = new ApproveRequestUseCaseImpl(repository, eventPublisher);
        return createTransactionalProxy(target, ApproveRequestUseCase.class, txManager);
    }

    @SuppressWarnings("unchecked")
    private <T> T createTransactionalProxy(Object target, Class<T> interfaceType, PlatformTransactionManager txManager) {
        ProxyFactory factory = new ProxyFactory(target);
        factory.addInterface(interfaceType);
        
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(txManager);
        interceptor.setTransactionAttributeSource(new MatchAlwaysTransactionAttributeSource());
        factory.addAdvice(interceptor);
        
        return (T) factory.getProxy();
    }
}

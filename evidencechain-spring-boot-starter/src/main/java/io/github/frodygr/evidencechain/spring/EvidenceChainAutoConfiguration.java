package io.github.frodygr.evidencechain.spring;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.core.export.ForensicReportExporter;
import io.github.frodygr.evidencechain.core.verifier.ChainVerifier;
import io.github.frodygr.evidencechain.spring.aop.AuditedEvidenceAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(EvidenceChainProperties.class)
@ConditionalOnProperty(prefix = "evidencechain", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EvidenceChainAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EvidenceChainLedger evidenceChainLedger() {
        return new EvidenceChainLedger();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChainVerifier chainVerifier() {
        return new ChainVerifier();
    }

    @Bean
    @ConditionalOnMissingBean
    public ForensicReportExporter forensicReportExporter() {
        return new ForensicReportExporter();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditedEvidenceAspect auditedEvidenceAspect(EvidenceChainLedger ledger) {
        return new AuditedEvidenceAspect(ledger);
    }
}

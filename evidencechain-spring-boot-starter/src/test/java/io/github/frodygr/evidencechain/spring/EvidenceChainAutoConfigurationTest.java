package io.github.frodygr.evidencechain.spring;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.spring.annotation.AuditedEvidence;
import io.github.frodygr.evidencechain.spring.aop.AuditedEvidenceAspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EvidenceChainAutoConfigurationTest.TestConfig.class)
class EvidenceChainAutoConfigurationTest {

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        public EvidenceChainLedger evidenceChainLedger() {
            return new EvidenceChainLedger();
        }

        @Bean
        public AuditedEvidenceAspect auditedEvidenceAspect(EvidenceChainLedger ledger) {
            return new AuditedEvidenceAspect(ledger);
        }

        @Bean
        public MockBankingService mockBankingService() {
            return new MockBankingService();
        }
    }

    @Service
    static class MockBankingService {
        @AuditedEvidence(action = "TRANSFER_FUNDS", resource = "BankLedger")
        public String executeTransfer(String recipient, double amount) {
            return "SUCCESS";
        }
    }

    @Autowired
    private MockBankingService bankingService;

    @Autowired
    private EvidenceChainLedger ledger;

    @Test
    @DisplayName("Should automatically record tamper-evident event via @AuditedEvidence")
    void shouldRecordEventViaAspect() {
        bankingService.executeTransfer("ES9121000418450200051332", 1500.00);

        assertThat(ledger.size()).isEqualTo(1);
        assertThat(ledger.getEvents().get(0).action()).isEqualTo("TRANSFER_FUNDS");
        assertThat(ledger.getEvents().get(0).resource()).isEqualTo("BankLedger");
        assertThat(ledger.verify().valid()).isTrue();
    }
}

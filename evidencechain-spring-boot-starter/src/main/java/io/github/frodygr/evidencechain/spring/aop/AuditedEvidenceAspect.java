package io.github.frodygr.evidencechain.spring.aop;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.spring.annotation.AuditedEvidence;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intercepts calls annotated with @AuditedEvidence and appends tamper-evident audit blocks to the ledger.
 */
@Aspect
public class AuditedEvidenceAspect {

    private final EvidenceChainLedger ledger;

    public AuditedEvidenceAspect(EvidenceChainLedger ledger) {
        this.ledger = ledger;
    }

    @Around("@annotation(auditedEvidence)")
    public Object interceptAudit(ProceedingJoinPoint joinPoint, AuditedEvidence auditedEvidence) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String action = !auditedEvidence.action().isEmpty() ? auditedEvidence.action() : signature.getMethod().getName();
        String resource = !auditedEvidence.resource().isEmpty() ? auditedEvidence.resource() : signature.getDeclaringType().getSimpleName();

        Map<String, String> metadata = new LinkedHashMap<>();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames != null && args != null) {
            for (int i = 0; i < Math.min(paramNames.length, args.length); i++) {
                metadata.put(paramNames[i], String.valueOf(args[i]));
            }
        }

        // Execute action
        Object result = joinPoint.proceed();

        // Record evidence event
        ledger.record("system-principal", action, resource, metadata);

        return result;
    }
}

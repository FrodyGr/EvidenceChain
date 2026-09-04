package io.github.frodygr.evidencechain.spring.annotation;

import java.lang.annotation.*;

/**
 * Declares that invocations of this method must be permanently recorded into the EvidenceChain cryptographic ledger.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditedEvidence {
    /**
     * Action name, e.g. "USER_DELETED", "PAYMENT_PROCESSED". Defaults to method name.
     */
    String action() default "";

    /**
     * Target resource type, e.g. "Account", "MedicalRecord". Defaults to enclosing class name.
     */
    String resource() default "";
}

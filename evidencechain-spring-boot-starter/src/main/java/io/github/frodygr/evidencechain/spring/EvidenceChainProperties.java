package io.github.frodygr.evidencechain.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "evidencechain")
public class EvidenceChainProperties {

    private boolean enabled = true;
    private String caseReference = "DEFAULT-AUDIT";
    private boolean autoExportOnShutdown = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCaseReference() {
        return caseReference;
    }

    public void setCaseReference(String caseReference) {
        this.caseReference = caseReference;
    }

    public boolean isAutoExportOnShutdown() {
        return autoExportOnShutdown;
    }

    public void setAutoExportOnShutdown(boolean autoExportOnShutdown) {
        this.autoExportOnShutdown = autoExportOnShutdown;
    }
}

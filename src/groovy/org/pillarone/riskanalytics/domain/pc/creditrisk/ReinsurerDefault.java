package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsurerDefault extends Packet {

    private String reinsurer;
    private boolean defaultOccurred = false;

    public ReinsurerDefault(String reinsurer, boolean defaultOccurred) {
        this.reinsurer = reinsurer;
        this.defaultOccurred = defaultOccurred;
    }

    public String getReinsurer() {
        return reinsurer;
    }

    public void setReinsurer(String reinsurer) {
        this.reinsurer = reinsurer;
    }

    public boolean isDefaultOccurred() {
        return defaultOccurred;
    }

    public void setDefaultOccurred(boolean defaultOccurred) {
        this.defaultOccurred = defaultOccurred;
    }
}
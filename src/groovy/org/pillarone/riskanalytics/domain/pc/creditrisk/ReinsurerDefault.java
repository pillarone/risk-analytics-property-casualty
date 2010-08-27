package org.pillarone.riskanalytics.domain.pc.creditrisk;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsurerDefault extends SingleValuePacket {

    private String reinsurer;
    private boolean defaultOccurred = false;

    public ReinsurerDefault() {
    }

    public ReinsurerDefault(String reinsurer, boolean defaultOccurred) {
        this.reinsurer = reinsurer;
        this.defaultOccurred = defaultOccurred;
        this.value = defaultOccurred ? 1d: 0d;
    }

    @Override
    public String toString() {
        return new StringBuffer(reinsurer).append(" is default: ").append(defaultOccurred).toString();
    }

    @Override
    public double getValue() {
        return defaultOccurred ? 1d : 0d;
    }

    @Override
    public void setValue(double value) {
        this.value = defaultOccurred ? 1d: 0d;
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
        this.value = defaultOccurred ? 1d: 0d;
    }
}
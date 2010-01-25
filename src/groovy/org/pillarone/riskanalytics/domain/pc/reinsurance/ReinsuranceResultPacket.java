package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceResultPacket extends MultiValuePacket {
    private double premium;
    private double claim;
    private double netCashFlow;
    private double result;

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public double getClaim() {
        return claim;
    }

    public void setClaim(double claim) {
        this.claim = claim;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public double getNetCashFlow() {
        return netCashFlow;
    }

    public void setNetCashFlow(double netCashFlow) {
        this.netCashFlow = netCashFlow;
    }
}

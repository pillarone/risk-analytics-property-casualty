package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingResult extends MultiValuePacket {
    public double result;
    public double premium;
    public double claim;
    public double commission;

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

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

    public double getCommission() {
        return commission;
    }

    public void setNetCommission(double commission) {
        this.commission = commission;
    }
}

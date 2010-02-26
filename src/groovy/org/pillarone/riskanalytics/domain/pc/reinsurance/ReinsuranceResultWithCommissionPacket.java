package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceResultWithCommissionPacket extends MultiValuePacket {
    private double result;
    private double cededPremium;
    private double cededClaim;
    private double cededCommission;

    public double getCededPremium() {
        return cededPremium;
    }

    public void setCededPremium(double cededPremium) {
        this.cededPremium = cededPremium;
        setResultingCosts();
    }

    public double getCededClaim() {
        return cededClaim;
    }

    public void setCededClaim(double cededClaim) {
        this.cededClaim = cededClaim;
        setResultingCosts();
    }

    public double getResult() {
        result = cededPremium + cededClaim + cededCommission;
        return result;
    }

    private void setResultingCosts() {
        result = cededPremium + cededClaim + cededCommission;
    }
    public void setResult(double result) {
        this.result = result;
    }

    public double getCededCommission() {
        return cededCommission;
    }

    public void setCededCommission(double cededCommission) {
        this.cededCommission = cededCommission;
        setResultingCosts();
    }
}
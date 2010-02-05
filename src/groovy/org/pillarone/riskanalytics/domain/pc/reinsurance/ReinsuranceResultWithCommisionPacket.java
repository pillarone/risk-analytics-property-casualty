package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceResultWithCommisionPacket extends MultiValuePacket {
    private double resultingCosts;
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

    public double getResultingCosts() {
        resultingCosts = -cededPremium + cededClaim + cededCommission;
        return resultingCosts;
    }

    private void setResultingCosts() {
        resultingCosts = -cededPremium + cededClaim + cededCommission;
    }
    public void setResultingCosts(double resultingCosts) {
        this.resultingCosts = resultingCosts;
    }

    public double getCededCommission() {
        return cededCommission;
    }

    public void setCededCommission(double cededCommission) {
        this.cededCommission = cededCommission;
        setResultingCosts();
    }
}
package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExperienceAccountPacket extends MultiValuePacket {

    private double premium;
    private double claim;
    private double balance;
    private double netCashFlow;

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getNetCashFlow() {
        return netCashFlow;
    }

    public void setNetCashFlow(double netCashFlow) {
        this.netCashFlow = netCashFlow;
    }
}

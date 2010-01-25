package org.pillarone.riskanalytics.domain.assets;

import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class FeesParameters extends Packet {

    private double bondTransactionCostsRate;
    private double bondDepositFeesPercentageOfMarketValue;

    public double getBondTransactionCostsRate() {
        return bondTransactionCostsRate;
    }

    public void setBondTransactionCostsRate(double bondTransactionCostsRate) {
        this.bondTransactionCostsRate = bondTransactionCostsRate;
    }

    public double getBondDepositFeesPercentageOfMarketValue() {
        return bondDepositFeesPercentageOfMarketValue;
    }

    public void setBondDepositFeesPercentageOfMarketValue(double bondDepositFeesPercentageOfMarketValue) {
        this.bondDepositFeesPercentageOfMarketValue = bondDepositFeesPercentageOfMarketValue;
    }
}

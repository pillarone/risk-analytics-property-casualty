package org.pillarone.riskanalytics.domain.assets;

import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class CashParameters extends Packet {

    private double initialCash;
    private double minimalCashLevel;
    private double maximalCashLevel;

    public double getInitialCash() {
        return initialCash;
    }

    public void setInitialCash(double initialCash) {
        this.initialCash = initialCash;
    }

    public double getMinimalCashLevel() {
        return minimalCashLevel;
    }

    public void setMinimalCashLevel(double minimalCashLevel) {
        this.minimalCashLevel = minimalCashLevel;
    }

    public double getMaximalCashLevel() {
        return maximalCashLevel;
    }

    public void setMaximalCashLevel(double maximalCashLevel) {
        this.maximalCashLevel = maximalCashLevel;
    }
}

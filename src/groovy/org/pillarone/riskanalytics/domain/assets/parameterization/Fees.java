package org.pillarone.riskanalytics.domain.assets.parameterization;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.assets.FeesParameters;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class Fees extends Component {

    private double parmBondTransactionCostsRate;
    private double parmBondDepositFeesPercentageOfMarketValue;

    private PacketList<FeesParameters> outFeesParameters = new PacketList<FeesParameters>(FeesParameters.class);


    protected void doCalculation() {
        FeesParameters packet = new FeesParameters();
        packet.setBondDepositFeesPercentageOfMarketValue(parmBondDepositFeesPercentageOfMarketValue);
        packet.setBondTransactionCostsRate(parmBondTransactionCostsRate);
        getOutFeesParameters().add(packet);
    }

    public double getParmBondTransactionCostsRate() {
        return parmBondTransactionCostsRate;
    }

    public void setParmBondTransactionCostsRate(double parmBondTransactionCostsRate) {
        this.parmBondTransactionCostsRate = parmBondTransactionCostsRate;
    }

    public double getParmBondDepositFeesPercentageOfMarketValue() {
        return parmBondDepositFeesPercentageOfMarketValue;
    }

    public void setParmBondDepositFeesPercentageOfMarketValue(double parmBondDepositFeesPercentageOfMarketValue) {
        this.parmBondDepositFeesPercentageOfMarketValue = parmBondDepositFeesPercentageOfMarketValue;
    }

    public PacketList<FeesParameters> getOutFeesParameters() {
        return outFeesParameters;
    }

    public void setOutFeesParameters(PacketList<FeesParameters> outFeesParameters) {
        this.outFeesParameters = outFeesParameters;
    }
}

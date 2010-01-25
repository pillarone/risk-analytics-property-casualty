package org.pillarone.riskanalytics.domain.assets.parameterization;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.assets.CashParameters;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class InitialCash extends Component {

    private double parmInitialCash;
    private double parmMinimumCashLevel;
    private double parmMaximumCashLevel;

    private PacketList<CashParameters> outCashParameters = new PacketList<CashParameters>(CashParameters.class);

    protected void doCalculation() {
        if (parmMaximumCashLevel < parmInitialCash || parmMinimumCashLevel > parmInitialCash) {
            throw new IllegalArgumentException("error: improper setting of the parameter, minimum and maximum must respectively be inferior and superior to the initial amount of cash");
        }
        CashParameters packet = new CashParameters();
        packet.setInitialCash(getParmInitialCash());
        packet.setMinimalCashLevel(getParmMinimumCashLevel());
        packet.setMaximalCashLevel(getParmMaximumCashLevel());
        outCashParameters.add(packet);
    }

    public double getParmInitialCash() {
        return parmInitialCash;
    }

    public void setParmInitialCash(double parmInitialCash) {
        this.parmInitialCash = parmInitialCash;
    }

    public double getParmMinimumCashLevel() {
        return parmMinimumCashLevel;
    }

    public void setParmMinimumCashLevel(double parmMinimumCashLevel) {
        this.parmMinimumCashLevel = parmMinimumCashLevel;
    }

    public double getParmMaximumCashLevel() {
        return parmMaximumCashLevel;
    }

    public void setParmMaximumCashLevel(double parmMaximumCashLevel) {
        this.parmMaximumCashLevel = parmMaximumCashLevel;
    }

    public PacketList<CashParameters> getOutCashParameters() {
        return outCashParameters;
    }

    public void setOutCashParameters(PacketList<CashParameters> outCashParameters) {
        this.outCashParameters = outCashParameters;
    }
}

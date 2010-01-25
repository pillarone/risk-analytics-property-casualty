package org.pillarone.riskanalytics.domain.assets;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class CashManagement extends Component {

    /*private AbstractContext simulationContext;
    private PeriodStore periodStore;
    private static final String CASH = "cash";
    private static final String CAPITAL_GAIN = "capital gain";*/

    private PacketList<CashParameters> inCashParameters = new PacketList<CashParameters>(CashParameters.class);
    private PacketList<CashParameters> outCashParameters = new PacketList<CashParameters>(CashParameters.class);
    private PacketList<SingleValuePacket> inCapitalGain = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<BondParameters> inBondParameters = new PacketList<BondParameters>(BondParameters.class); //get the Bonds purchase prices
    private PacketList<BondParameters> outBondParameters = new PacketList<BondParameters>(BondParameters.class);

    //private PL<> inAllocationParameters : this packet will enable us to chose between assets
    //private PL<> inInitialAllocationParameters : this packet will give us the initial portfolio
    //private PL<> outAllocationParameters...

    protected void doCalculation() {//todo (cne) here will be the cash management system.
        //todo (cne) this component is for now unused. Discuss whether it is relevant to keep it


        CashParameters cashPacket = inCashParameters.get(0);
        SingleValuePacket capitalGainPacket = inCapitalGain.get(0);


        double availableCash = cashPacket.getInitialCash() - cashPacket.getMinimalCashLevel() + capitalGainPacket.getValue();
        double numberOfBondsPurchased = 0;

        //todo(cne) only bonds are available, the cash model is therefore simple, other assets have to be implemented to get a relevant allocation

        if (availableCash > cashPacket.getMaximalCashLevel()) {
            //set new initial, minimal, maximal cash levels, return purchase order
            for (BondParameters bond : inBondParameters) {

                while (availableCash - bond.getPurchasePrice() > cashPacket.getMinimalCashLevel()) {
                    availableCash -= bond.getPurchasePrice();
                    numberOfBondsPurchased++;

                }
                BondParameters packet = new BondParameters();
                packet.setQuantity(bond.getQuantity() + numberOfBondsPurchased);
                outBondParameters.add(packet);
            }


        } else {
            // don't do anything : deactivate component, or return packet with a "deactivation" flag
        }
        CashParameters packet = new CashParameters();
        //packet
        outCashParameters.add(packet);


    }

    public PacketList<CashParameters> getInCashParameters() {
        return inCashParameters;
    }

    public void setInCashParameters(PacketList<CashParameters> inCashParameters) {
        this.inCashParameters = inCashParameters;
    }

    public PacketList<CashParameters> getOutCashParameters() {
        return outCashParameters;
    }

    public void setOutCashParameters(PacketList<CashParameters> outCashParameters) {
        this.outCashParameters = outCashParameters;
    }

    public PacketList<SingleValuePacket> getInCapitalGain() {
        return inCapitalGain;
    }

    public void setInCapitalGain(PacketList<SingleValuePacket> inCapitalGain) {
        this.inCapitalGain = inCapitalGain;
    }

    public PacketList<BondParameters> getInBondParameters() {
        return inBondParameters;
    }

    public void setInBondParameters(PacketList<BondParameters> inBondParameters) {
        this.inBondParameters = inBondParameters;
    }

    public PacketList<BondParameters> getOutBondParameters() {
        return outBondParameters;
    }

    public void setOutBondParameters(PacketList<BondParameters> outBondParameters) {
        this.outBondParameters = outBondParameters;
    }
}

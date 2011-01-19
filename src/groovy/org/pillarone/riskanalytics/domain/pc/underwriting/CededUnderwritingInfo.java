package org.pillarone.riskanalytics.domain.pc.underwriting;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class CededUnderwritingInfo extends UnderwritingInfo {

//    private double fixedPremium;
//    private double variablePremium;

//    private double commission;
//    private double fixedCommission;
//    private double variableCommission;


    public CededUnderwritingInfo copy() {
        CededUnderwritingInfo copy = CededUnderwritingInfoPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    public UnderwritingInfo copyToSuperClass(){
        UnderwritingInfo copy = UnderwritingInfoPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    public void set(CededUnderwritingInfo underwritingInfo) {
        super.set(underwritingInfo);
//            fixedPremium = underwritingInfo.fixedPremium;
//            variablePremium = underwritingInfo.variablePremium;
//            commission = underwritingInfo.commission;
//            fixedCommission = underwritingInfo.fixedCommission;
//            variableCommission = underwritingInfo.variableCommission;

    }

  /*  public double getFixedPremium() {
        return fixedPremium;
    }

    public void setFixedPremium(double fixedPremium) {
        this.fixedPremium = fixedPremium;
    }

    public double getVariablePremium() {
        return variablePremium;
    }

    public void setVariablePremium(double variablePremium) {
        this.variablePremium = variablePremium;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getFixedCommission() {
        return fixedCommission;
    }

    public void setFixedCommission(double fixedCommission) {
        this.fixedCommission = fixedCommission;
    }

    public double getVariableCommission() {
        return variableCommission;
    }

    public void setVariableCommission(double variableCommission) {
        this.variableCommission = variableCommission;
    }   */
}

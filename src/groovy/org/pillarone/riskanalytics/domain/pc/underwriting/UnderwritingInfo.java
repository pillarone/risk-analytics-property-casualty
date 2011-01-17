package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfo extends ExposureInfo {

    public UnderwritingInfo originalUnderwritingInfo;
    public double premiumWritten;
    private double fixedPremium;
    private double variablePremium;
    public double commission;
    private double fixedCommission;
    private double variableCommission;
    private LobMarker lineOfBusiness;
    private IReinsuranceContractMarker reinsuranceContract;
    
    private static final String PREMIUM = "premium";
    private static final String COMMISSION = "commission";

    public UnderwritingInfo() {
        super();
    }

    @Override
    public UnderwritingInfo copy() {
        UnderwritingInfo copy = UnderwritingInfoPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    public void set(UnderwritingInfo underwritingInfo) {
        setOrigin(underwritingInfo.getOrigin());
        setExposureDefinition(underwritingInfo.exposureDefinition);
        setPremiumWrittenAsIf(underwritingInfo.getPremiumWrittenAsIf());
        setNumberOfPolicies(underwritingInfo.getNumberOfPolicies());
        setSumInsured(underwritingInfo.getSumInsured());
        setMaxSumInsured(underwritingInfo.getMaxSumInsured());
        setOriginalUnderwritingInfo(underwritingInfo.getOriginalUnderwritingInfo());
        setPremiumWritten(underwritingInfo.premiumWritten);
        setFixedPremium(underwritingInfo.fixedPremium);
        setVariablePremium(underwritingInfo.variablePremium);
        setCommission(underwritingInfo.commission);
        setFixedCommission(underwritingInfo.fixedCommission);
        setVariableCommission(underwritingInfo.variableCommission);
        setLineOfBusiness(underwritingInfo.getLineOfBusiness());
        setReinsuranceContract(underwritingInfo.getReinsuranceContract());
    }

    /**
     * Warning: if the number or names of values is modified, UnderwritingBatchInsertDBCollector
     * has to be corrected accordingly.
     *
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> map = new HashMap<String, Number>();
        map.put(PREMIUM, premiumWritten);
        map.put(COMMISSION, commission);
        return map;
    }

    @Override
    public List<String> getFieldNames() {
        return Arrays.asList(PREMIUM, COMMISSION);
    }

    public double scaleValue(Exposure base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case PREMIUM_WRITTEN:
                return getPremiumWritten();
            case NUMBER_OF_POLICIES:
                return getNumberOfPolicies();
        }
        return 0;
    }

    /**
     * Adds additive UnderwritingInfo fields (premiumWritten, commission) as well as combining ExposureInfo fields.
     * @param other
     * @return UnderwritingInfo packet with resulting fields
     */
    public void plus(UnderwritingInfo other) {
        super.plus(other);
        premiumWritten += other.premiumWritten;
        commission += other.commission;
        fixedPremium += other.fixedPremium;
        variablePremium += other.variablePremium;
        fixedCommission += other.fixedCommission;
        variableCommission += other.variableCommission;
    }

    public void minus(UnderwritingInfo other) {
        super.minus(other);
        premiumWritten -= other.premiumWritten;
        commission -= other.commission;
        fixedPremium -= other.fixedPremium;
        variablePremium -= other.variablePremium;
        fixedCommission -= other.fixedCommission;
        variableCommission -= other.variableCommission;
        if (premiumWritten == 0 && commission == 0 && sumInsured == 0) {
            numberOfPolicies = 0;
        }
    }

    public void scale(double factor) {
        super.scale(factor);
        commission *= factor;
        premiumWritten *= factor;
        fixedPremium *= factor;
        variablePremium *= factor;
        fixedCommission *= factor;
        variableCommission *= factor;
    }

    public double getPremiumWritten() {
        return premiumWritten;
    }

    public void setPremiumWritten(double premiumWritten) {
        this.premiumWritten = premiumWritten;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public UnderwritingInfo getOriginalUnderwritingInfo() {
        return originalUnderwritingInfo;
    }

    public void setOriginalUnderwritingInfo(UnderwritingInfo originalUnderwritingInfo) {
        this.originalUnderwritingInfo = originalUnderwritingInfo;
    }

    public String toString() {
        return "premium: " + String.valueOf(premiumWritten) + ", commission: " + commission +
                ", origin: " + (origin == null ? "null" : origin.getNormalizedName()) +
                ", original " + (originalUnderwritingInfo==null ? "null" :
                                 originalUnderwritingInfo.origin==null ? "unnamed" :
                                 originalUnderwritingInfo.origin.getNormalizedName());
    }

    public LobMarker getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(LobMarker lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public IReinsuranceContractMarker getReinsuranceContract() {
        return reinsuranceContract;
    }

    public void setReinsuranceContract(IReinsuranceContractMarker reinsuranceContract) {
        this.reinsuranceContract = reinsuranceContract;
    }

    public double getFixedPremium() {
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
    }
}

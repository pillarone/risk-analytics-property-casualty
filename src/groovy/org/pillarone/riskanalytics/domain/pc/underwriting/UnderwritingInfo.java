package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;

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
    public double commission;
    private LobMarker lineOfBusiness;
    
    private static final String PREMIUM = "premium";
    private static final String COMMISSION = "commission";

    public UnderwritingInfo() {
        super();
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

    public UnderwritingInfo plus(UnderwritingInfo other) {
        super.plus(other);
        premiumWritten += other.premiumWritten;
        commission += other.commission;
        return this;
    }

    public UnderwritingInfo minus(UnderwritingInfo other) {
        super.minus(other);
        premiumWritten -= other.premiumWritten;
        commission -= other.commission;
        return this;
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
        if (origin == null || originalUnderwritingInfo == null) {
            return "premium: " + String.valueOf(premiumWritten) + ", commission: " + commission;
        }
        else {
            return "premium: " + String.valueOf(premiumWritten) + ", commission: " + commission + ", origin: " + origin.getNormalizedName() + ", original " + originalUnderwritingInfo.origin.getNormalizedName();
        }
    }

    public LobMarker getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(LobMarker lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }
}
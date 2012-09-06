package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.core.simulation.InvalidParameterException;
import org.pillarone.riskanalytics.domain.pc.constants.Exposure;

/**
 * @author Michael-Noe (at) Web (dot) de
 */
public class UnderwritingInfoWithLossProbability extends UnderwritingInfo {
    public double lossProbability;
    public UnderwritingInfoWithLossProbability originalUnderwritingInfoWLP;

    public UnderwritingInfoWithLossProbability() {
        super();
    }

    public double scaleValue(Exposure base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case PREMIUM_WRITTEN:
                return getPremium();
            case NUMBER_OF_POLICIES:
                return getNumberOfPolicies();
            default:
                throw new InvalidParameterException(base + " not implemented");
        }
    }

    public void plus(UnderwritingInfoWithLossProbability other) {
        super.plus(other);
    }

    public void minus(UnderwritingInfoWithLossProbability other) {
        super.minus(other);
    }

    public double getLossProbability() {
        return lossProbability;
    }

    public void setLossProbability(double lossProbability) {
        this.lossProbability = lossProbability;
    }

    public UnderwritingInfoWithLossProbability getOriginalUnderwritingInfoWithLossProbability() {
        return originalUnderwritingInfoWLP;
    }

    public void setOriginalUnderwritingInfoWithLossProbability(UnderwritingInfoWithLossProbability originalUnderwritingInfoWLP) {
        this.originalUnderwritingInfoWLP = originalUnderwritingInfoWLP;
    }

}

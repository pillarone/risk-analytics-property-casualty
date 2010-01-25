package org.pillarone.riskanalytics.domain.pc.underwriting;

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
                return getPremiumWritten();
            case NUMBER_OF_POLICIES:
                return getNumberOfPolicies();
        }
        return 0;
    }

    public UnderwritingInfoWithLossProbability plus(UnderwritingInfoWithLossProbability other) {
        super.plus(other);
        return this;
    }

    public UnderwritingInfoWithLossProbability minus(UnderwritingInfoWithLossProbability other) {
        super.minus(other);
        return this;
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

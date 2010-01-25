package org.pillarone.riskanalytics.domain.pc.underwriting;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GrossCededUnderwritingInfoPair {
    private UnderwritingInfo underwritingInfoGross;
    private UnderwritingInfo underwritingInfoCeded;

    public GrossCededUnderwritingInfoPair(UnderwritingInfo underwritingInfoGross) {
        this.underwritingInfoGross = underwritingInfoGross;
    }

    public GrossCededUnderwritingInfoPair(UnderwritingInfo underwritingInfoGross, UnderwritingInfo underwritingInfoCeded) {
        this.underwritingInfoGross = underwritingInfoGross;
        this.underwritingInfoCeded = underwritingInfoCeded;
    }

    public UnderwritingInfo getUnderwritingInfoGross() {
        return underwritingInfoGross;
    }

    public void setUnderwritingInfoGross(UnderwritingInfo underwritingInfoGross) {
        this.underwritingInfoGross = underwritingInfoGross;
    }

    public UnderwritingInfo getUnderwritingInfoCeded() {
        return underwritingInfoCeded;
    }

    public void setUnderwritingInfoCeded(UnderwritingInfo underwritingInfoCeded) {
        this.underwritingInfoCeded = underwritingInfoCeded;
    }
}

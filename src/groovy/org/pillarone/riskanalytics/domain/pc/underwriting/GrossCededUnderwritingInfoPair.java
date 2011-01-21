package org.pillarone.riskanalytics.domain.pc.underwriting;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GrossCededUnderwritingInfoPair {
    private UnderwritingInfo underwritingInfoGross;
    private CededUnderwritingInfo underwritingInfoCeded;

    public GrossCededUnderwritingInfoPair(UnderwritingInfo underwritingInfoGross) {
        this.underwritingInfoGross = underwritingInfoGross;
    }

    public GrossCededUnderwritingInfoPair(UnderwritingInfo underwritingInfoGross, CededUnderwritingInfo underwritingInfoCeded) {
        this.underwritingInfoGross = underwritingInfoGross;
        this.underwritingInfoCeded = underwritingInfoCeded;
    }

    public UnderwritingInfo getUnderwritingInfoGross() {
        return underwritingInfoGross;
    }

    public void setUnderwritingInfoGross(UnderwritingInfo underwritingInfoGross) {
        this.underwritingInfoGross = underwritingInfoGross;
    }

    public CededUnderwritingInfo getUnderwritingInfoCeded() {
        return underwritingInfoCeded;
    }

    public void setUnderwritingInfoCeded(CededUnderwritingInfo underwritingInfoCeded) {
        this.underwritingInfoCeded = underwritingInfoCeded;
    }
}

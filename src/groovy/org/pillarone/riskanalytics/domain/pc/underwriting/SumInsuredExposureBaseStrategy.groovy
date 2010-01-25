package org.pillarone.riskanalytics.domain.pc.underwriting

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SumInsuredExposureBaseStrategy extends DependingExposureBaseStrategy {


    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        return underwritingInfos.sumInsured.sum();
    }

    public Object getType() {
        return ExposureBaseType.SUMINSURED;
    }
}
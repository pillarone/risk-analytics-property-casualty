package org.pillarone.riskanalytics.domain.pc.underwriting

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NumberOfPoliciesExposureBaseStrategy extends DependingExposureBaseStrategy {


    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        return underwritingInfos.numberOfPolicies.sum();
    }

    public Object getType() {
        return ExposureBaseType.NUMBEROFPOLICIES;
    }
}
package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SumInsuredExposureBaseStrategy extends DependingExposureBaseStrategy {


    public double scaleFactor(List<UnderwritingInfo> underwritingInfos) {
        return underwritingInfos.sumInsured.sum();
    }

    public IParameterObjectClassifier getType() {
        return ExposureBaseType.SUMINSURED;
    }
}
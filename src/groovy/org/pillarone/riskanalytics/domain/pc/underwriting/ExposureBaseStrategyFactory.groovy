package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureBaseStrategyFactory {
    private static IExposureBaseStrategy getAbsolute() {
        return new AbsoluteExposureBaseStrategy()
    }

    private static IExposureBaseStrategy getPremiumWritten(ComboBoxTableMultiDimensionalParameter underwritingInformation) {
        return new PremiumWrittenExposureBaseStrategy(underwritingInformation: underwritingInformation)
    }

    private static IExposureBaseStrategy getNumberOfPolicies(ComboBoxTableMultiDimensionalParameter underwritingInformation) {
        return new NumberOfPoliciesExposureBaseStrategy(underwritingInformation: underwritingInformation)
    }

    private static IExposureBaseStrategy getSumInsured(ComboBoxTableMultiDimensionalParameter underwritingInformation) {
        return new SumInsuredExposureBaseStrategy(underwritingInformation: underwritingInformation)
    }

    static IExposureBaseStrategy getStrategy(ExposureBaseType type, Map parameters) {
        IExposureBaseStrategy exposureBase
        switch (type) {
            case ExposureBaseType.ABSOLUTE:
                exposureBase = getAbsolute()
                break
            case ExposureBaseType.PREMIUMWRITTEN:
                exposureBase = getPremiumWritten(parameters['underwritingInformation'])
                break
            case ExposureBaseType.NUMBEROFPOLICIES:
                exposureBase = getNumberOfPolicies(parameters['underwritingInformation'])
                break
            case ExposureBaseType.SUMINSURED:
                exposureBase = getSumInsured(parameters['underwritingInformation'])
                break
        }
        return exposureBase
    }
}
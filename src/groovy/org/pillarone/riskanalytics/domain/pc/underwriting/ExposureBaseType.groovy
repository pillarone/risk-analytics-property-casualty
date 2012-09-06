package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.simulation.InvalidParameterException

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureBaseType extends AbstractParameterObjectClassifier {


    public static final ExposureBaseType ABSOLUTE = new ExposureBaseType("absolute", "ABSOLUTE", [:])
    public static final ExposureBaseType PREMIUMWRITTEN = new ExposureBaseType("premium written", "PREMIUMWRITTEN", [
        underwritingInformation: new ComboBoxTableMultiDimensionalParameter([''], ["Underwriting Info"], IUnderwritingInfoMarker)
    ])
    public static final ExposureBaseType NUMBEROFPOLICIES = new ExposureBaseType("number of policies", "NUMBEROFPOLICIES", [
        underwritingInformation: new ComboBoxTableMultiDimensionalParameter([''], ["Underwriting Info"], IUnderwritingInfoMarker)
    ])
    public static final ExposureBaseType SUMINSURED = new ExposureBaseType("sum insured", "SUMINSURED", [
        underwritingInformation: new ComboBoxTableMultiDimensionalParameter([''], ["Underwriting Info"], IUnderwritingInfoMarker)
    ])

    public static final all = [ABSOLUTE, PREMIUMWRITTEN, NUMBEROFPOLICIES, SUMINSURED]

    protected static Map types = [:]
    static {
        ExposureBaseType.all.each {
            ExposureBaseType.types[it.toString()] = it
        }
    }

    private ExposureBaseType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ExposureBaseType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return ExposureBaseType.getStrategy(this, parameters)
    }

    static IExposureBaseStrategy getStrategy(ExposureBaseType type, Map parameters) {
        IExposureBaseStrategy exposureBase
        switch (type) {
            case ExposureBaseType.ABSOLUTE:
                exposureBase = new AbsoluteExposureBaseStrategy()
                break
            case ExposureBaseType.PREMIUMWRITTEN:
                exposureBase = new PremiumWrittenExposureBaseStrategy(underwritingInformation: (ComboBoxTableMultiDimensionalParameter) parameters['underwritingInformation'])
                break
            case ExposureBaseType.NUMBEROFPOLICIES:
                exposureBase = new NumberOfPoliciesExposureBaseStrategy(underwritingInformation: (ComboBoxTableMultiDimensionalParameter) parameters['underwritingInformation'])
                break
            case ExposureBaseType.SUMINSURED:
                exposureBase = new SumInsuredExposureBaseStrategy(underwritingInformation: (ComboBoxTableMultiDimensionalParameter) parameters['underwritingInformation'])
                break
            default:
                throw new InvalidParameterException("ExposureBaseType $type not implemented")
        }
        return exposureBase
    }
}
package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

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
        return ExposureBaseStrategyFactory.getStrategy(this, parameters)
    }

    public String getConstructionString(Map parameters) {
        StringBuffer parameterString = new StringBuffer('[')
        parameters.each {k, v ->
            if (v.class.isEnum()) {
                parameterString << "\"$k\":${v.class.name}.$v,"
            } else if (v instanceof IParameterObject) {
                parameterString << "\"$k\":${v.type.getConstructionString(v.parameters)},"
            } else {
                parameterString << "\"$k\":$v,"
            }
        }
        if (parameterString.size() == 1) {
            parameterString << ':'
        }
        parameterString << ']'
        return "org.pillarone.riskanalytics.domain.pc.underwriting.ExposureBaseStrategyFactory.getStrategy(${this.class.name}.${typeName.toUpperCase()}, ${parameterString})"
    }
}
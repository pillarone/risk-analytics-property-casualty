package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

abstract public class DependingExposureBaseStrategy implements IExposureBaseStrategy, IParameterObject {

    ComboBoxTableMultiDimensionalParameter underwritingInformation = new ComboBoxTableMultiDimensionalParameter([''],
            ["Underwriting Info"], IUnderwritingInfoMarker)

    public abstract IParameterObjectClassifier getType()

    public Map getParameters() {
        ['underwritingInformation': underwritingInformation]
    }

    public abstract double scaleFactor(List<UnderwritingInfo> underwritingInfos)
}
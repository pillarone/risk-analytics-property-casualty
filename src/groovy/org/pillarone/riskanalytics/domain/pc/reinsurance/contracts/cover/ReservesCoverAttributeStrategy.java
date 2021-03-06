package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReservesCoverAttributeStrategy extends AbstractParameterObject implements IReservesCoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter reserves
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Reserves"), IReserveMarker.class);

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.RESERVES;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("reserves", reserves);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getReserves() {
        return reserves;
    }
}

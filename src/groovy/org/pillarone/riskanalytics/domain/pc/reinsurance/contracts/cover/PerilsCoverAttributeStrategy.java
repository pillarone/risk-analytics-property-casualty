package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilsCoverAttributeStrategy extends AbstractParameterObject implements IPerilCoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter perils
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Perils"), IPerilMarker.class);

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.PERILS;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("perils", perils);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getPerils() {
        return perils;
    }
}

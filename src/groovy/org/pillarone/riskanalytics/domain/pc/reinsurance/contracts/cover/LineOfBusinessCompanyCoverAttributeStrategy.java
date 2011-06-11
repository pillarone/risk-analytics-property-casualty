package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class LineOfBusinessCompanyCoverAttributeStrategy extends AbstractParameterObject implements ILinesOfBusinessCoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter lines = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Segments"), ISegmentMarker.class);

    public IParameterObjectClassifier getType() {
        return CompanyCoverAttributeStrategyType.LINESOFBUSINESS;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("lines", lines);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getLines() {
        return lines;
    }
}

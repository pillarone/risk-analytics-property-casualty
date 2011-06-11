package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class LineOfBusinessPerilsCompanyCoverAttributeStrategy extends AbstractParameterObject
        implements ILinesOfBusinessCoverAttributeStrategy, IPerilCoverAttributeStrategy, ICombinedCoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter lines
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Segments"), ISegmentMarker.class);
    private LogicArguments connection = LogicArguments.AND;
    private ComboBoxTableMultiDimensionalParameter perils
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Perils"), IPerilMarker.class);

    public IParameterObjectClassifier getType() {
        return CompanyCoverAttributeStrategyType.LINESOFBUSINESSPERILS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("connection", connection);
        parameters.put("lines", lines);
        parameters.put("perils", perils);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getLines() {
        return lines;
    }

    public ComboBoxTableMultiDimensionalParameter getPerils() {
        return perils;
    }

    public LogicArguments getConnection() {
        return connection;
    }
}

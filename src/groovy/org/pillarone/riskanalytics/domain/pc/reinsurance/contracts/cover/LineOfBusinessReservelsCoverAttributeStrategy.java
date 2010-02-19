package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LineOfBusinessReservelsCoverAttributeStrategy implements ICoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter lines
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Lines"), LobMarker.class);
    private LogicArguments connection = LogicArguments.AND;
    private ComboBoxTableMultiDimensionalParameter perils
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Perils"), PerilMarker.class);

    public Object getType() {
        return CoverAttributeStrategyType.LINESOFBUSINESSPERILS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("connection", connection);
        parameters.put("lines", lines);
        parameters.put("perils", perils);
        return parameters;
    }
}
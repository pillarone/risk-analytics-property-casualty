package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker;

import java.util.*;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ReservesCompanyCoverAttributeStrategy extends AbstractParameterObject implements IReservesCoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter reserves
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Reserves"), IReserveMarker.class);

    public IParameterObjectClassifier getType() {
        return CompanyCoverAttributeStrategyType.RESERVES;
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

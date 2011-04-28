package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class CompaniesCompanyCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter companies
            = new ComboBoxTableMultiDimensionalParameter(Collections.emptyList(), Arrays.asList("Covered Companies"), ICompanyMarker.class);

    public IParameterObjectClassifier getType() {
        return CompanyCoverAttributeStrategyType.COMPANIES;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("companies", companies);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getCompanies() {
        return companies;
    }
}


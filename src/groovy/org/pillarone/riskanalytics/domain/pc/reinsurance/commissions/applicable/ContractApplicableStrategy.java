package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class ContractApplicableStrategy extends AbstractParameterObject implements IContractApplicableStrategy {

    private ComboBoxTableMultiDimensionalParameter applicableContracts = new ComboBoxTableMultiDimensionalParameter(
        Collections.emptyList(), Arrays.asList("Applicable Contracts"), IReinsuranceContractMarker.class);

    public IParameterObjectClassifier getType() {
        return ApplicableStrategyType.CONTRACT;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("applicableContracts", applicableContracts);
        return parameters;
    }

    public ComboBoxTableMultiDimensionalParameter getApplicableContracts() {
        return applicableContracts;
    }
//
//    public void setApplicableContracts(ComboBoxTableMultiDimensionalParameter applicableContracts) {
//        this.applicableContracts = applicableContracts;
//    }
}

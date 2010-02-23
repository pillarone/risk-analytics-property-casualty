package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public interface IContractApplicableStrategy extends IApplicableStrategy {
    ComboBoxTableMultiDimensionalParameter getApplicableContracts();
}
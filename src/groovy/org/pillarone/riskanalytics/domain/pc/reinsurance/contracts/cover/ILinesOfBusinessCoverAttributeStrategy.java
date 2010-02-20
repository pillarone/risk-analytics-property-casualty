package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ILinesOfBusinessCoverAttributeStrategy extends ICoverAttributeStrategy {

    ComboBoxTableMultiDimensionalParameter getLines();
}

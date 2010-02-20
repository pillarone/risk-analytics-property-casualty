package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover;

import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICombinedCoverAttributeStrategy extends ICoverAttributeStrategy {
    LogicArguments getConnection();
}

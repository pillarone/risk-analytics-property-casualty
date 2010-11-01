package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategyWithPremiumAllocation extends IReinsuranceContractStrategy {

    /** Strategy to allocate the ceded premium to the different lines of business      */
  IPremiumAllocationStrategy getPremiumAllocation();

}
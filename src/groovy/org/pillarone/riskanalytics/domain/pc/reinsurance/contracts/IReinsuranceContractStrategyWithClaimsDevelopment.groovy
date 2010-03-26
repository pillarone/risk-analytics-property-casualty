package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket

/**
 *  Special methods to calculate the effects of a reinsurance contract
 *  on a series of claims with development information (paid, reserved).
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
interface IReinsuranceContractStrategyWithClaimsDevelopment extends IReinsuranceContractStrategy {
    /**
     *  Allocates part of the previously calculated paid portion of an aggregated covered claim
     *  for a reinsurance contract to back to each individual claim within the aggregate.
     */
    double allocateCededPaid(ClaimDevelopmentPacket inClaim);
    double allocateCededPaid(ClaimDevelopmentLeanPacket grossClaim);
}
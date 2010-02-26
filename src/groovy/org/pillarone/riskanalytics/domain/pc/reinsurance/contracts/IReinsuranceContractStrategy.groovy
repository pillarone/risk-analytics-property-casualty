package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
interface IReinsuranceContractStrategy {

    /** cave: current implementation works only for periods of one year length            */
    void initBookKeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo);

    /**
     *  Calculates the claim covered of the loss net after contracts with
     *  a smaller inuring priority or preceeding contracts in the net.
     */
    // todo(sku): replace with a new function double calculateCededShare(Claim grossClaim)
    double calculateCoveredLoss(Claim grossClaim);

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossInfo, double initialReserves);

    void resetMemberInstances();
}
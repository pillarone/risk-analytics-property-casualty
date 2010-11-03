package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {

    /** cave: current implementation works only for periods of one year length */
    void initBookkeepingFigures(List<Claim> inClaims, List<UnderwritingInfo> coverUnderwritingInfo);

    void initCededPremiumAllocation(List<Claim> cededClaims, List<UnderwritingInfo> grossUnderwritingInfos);

    /**
     *  Calculates the claim covered of the loss net after contracts with
     *  a smaller inuring priority or preceding contracts in the net.
     */
    // todo(sku): replace with a new function double calculateCededShare(Claim grossClaim)
    double allocateCededClaim(Claim grossClaim);

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossInfo, double initialReserves);

    void resetMemberInstances();

    /** returns the current value of coveredByReinsurer */
    double covered();

    /** multiplies coveredByReinsurer with the factor */
    void adjustCovered(double factor);

    /** resets coveredByReinsurer to the original value */
    void resetCovered();
}
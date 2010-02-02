package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
interface IReinsuranceContractStrategy extends IParameterObject {
    /** cave: current implementation works only for periods of one year length            */
    void initBookKeepingFigures(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfo);

    /** executed once per iteration, in the first period with a none trivial strategy parameterization */
    void initBookKeepingFiguresForIteration(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfo);
    /** executed for every period a contract has covered and is not exhausted */
    void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer);

    /**
     *  Calculates the claim covered of the loss net after contracts with
     *  a smaller inuring priority or preceding contracts in the net.
     */
    Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer);

    UnderwritingInfo calculateCoverUnderwritingInfo(UnderwritingInfo grossUnderwritingInfo, double coveredByReinsurer);

    // todo(sku): might be integrated in initBookKeepingFiguresForIteration()
    void resetMemberInstances();

    IReinsuranceContractStrategy copy();

    /**
     *
     * @return true if no more cover is available, including reinstatements
     */
    boolean exhausted();
}
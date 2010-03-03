package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class WXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy, IParameterObject {

    /**
     *  The keys are the original gross claims sorting from claims development component. The value contains the
     *  corresponding cumulative ceded claims created within this contract. Only incurred and paid are updated!
     */
    Map<Claim, ClaimDevelopmentPacket> originalClaimCumulativeCededClaim = new HashMap<Claim, ClaimDevelopmentPacket>();
    Map<Claim, ClaimDevelopmentPacket> originalClaimCumulativeGrossClaim = new HashMap<Claim, ClaimDevelopmentPacket>();

    ReinsuranceContractType getType() {
        ReinsuranceContractType.WXL;
    }

    Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy()
        cededClaim.scale(0)
        if (grossClaim.claimType.equals(ClaimType.SINGLE)) {
            if (grossClaim instanceof ClaimDevelopmentPacket) {
                // get cumulative gross and ceded claim using the reference of the original claim
                ClaimDevelopmentPacket cumulativeCededClaim = originalClaimCumulativeCededClaim.get(grossClaim.getOriginalClaim());
                ClaimDevelopmentPacket cumulativeGrossClaim = originalClaimCumulativeGrossClaim.get(grossClaim.getOriginalClaim());
                if (isIncurredPeriod(cumulativeGrossClaim) && !exhausted()) {
                    cumulativeCededClaim = (ClaimDevelopmentPacket) grossClaim.copy()
                    cumulativeCededClaim.scale(0)
                    cumulativeGrossClaim = (ClaimDevelopmentPacket) grossClaim.copy()
                    cumulativeGrossClaim.setPaid 0
                    cumulativeGrossClaim.setReserved 0
                    cumulativeGrossClaim.setChangeInReserves 0
                    cededClaim.incurred = calculateCededIncurredValueAndUpdateCumulativeClaims(cumulativeGrossClaim, cumulativeCededClaim, coveredByReinsurer)
                    if (grossClaim.originalClaim == null) {
                        originalClaimCumulativeCededClaim.put(grossClaim, cumulativeCededClaim)
                        originalClaimCumulativeGrossClaim.put(grossClaim, cumulativeGrossClaim)
                    }
                    else {
                        originalClaimCumulativeCededClaim.put(grossClaim.originalClaim, cumulativeCededClaim)
                        originalClaimCumulativeGrossClaim.put(grossClaim.originalClaim, cumulativeGrossClaim)
                    }

                }
                else if (cumulativeCededClaim == null && exhausted()) {
                    return cededClaim
                }
                cumulativeGrossClaim.paid += grossClaim.paid
                cededClaim.paid = calculateCededPaidValueAndUpdateCumulativeClaims(cumulativeGrossClaim, cumulativeCededClaim, coveredByReinsurer)
                cededClaim.reserved = cumulativeCededClaim.incurred - cumulativeCededClaim.paid
                cededClaim.changeInReserves = cededClaim.paid
            }
            else if (grossClaim instanceof Claim) {
                cededClaim.ultimate = calculateCoveredUltimate(grossClaim, coveredByReinsurer)
            }
        }
        return cededClaim
    }

    private boolean isIncurredPeriod(ClaimDevelopmentPacket claim) {
        return claim == null
    }  
}
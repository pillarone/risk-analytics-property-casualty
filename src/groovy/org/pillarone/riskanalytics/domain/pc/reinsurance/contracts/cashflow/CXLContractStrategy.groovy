package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow;


import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CXLContractStrategy extends XLContractStrategy implements IReinsuranceContractStrategy {

    /**
     *  The keys are the original gross claims sorting from claims development component.
     *  These maps are necessary for a correct calculation of the reserves.
     */
    Map<ClaimDevelopmentPacket, Double> remainingCededReserves = new HashMap<ClaimDevelopmentPacket, Double>();
    /**
     *  The value contains the
     *  corresponding cumulative ceded claims created within this contract. Only incurred and paid are updated!
     */
    private Map<Event, ClaimDevelopmentPacket> originalAggregateEventClaimCumulativeCededClaim = new HashMap<Event, ClaimDevelopmentPacket>();
    private Map<Event, ClaimDevelopmentPacket> originalAggregateEventClaimCumulativeGrossClaim = new HashMap<Event, ClaimDevelopmentPacket>();


    private Map<Event, Claim> grossClaimsAggregatedByEvent = new LinkedHashMap<Event, Claim>()
    // the value objects contain ratios, not absolute values
    private Map<Event, Claim> cededRatiosByEvent = new LinkedHashMap<Event, Claim>()

    ReinsuranceContractType getType() {
        ReinsuranceContractType.CXL
    }

    void initBookKeepingFiguresForIteration(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos) {
        super.initBookKeepingFiguresForIteration(grossClaims, grossUnderwritingInfos)
        originalAggregateEventClaimCumulativeCededClaim.clear()
        originalAggregateEventClaimCumulativeGrossClaim.clear()
        remainingCededReserves.clear()
    }

    void initBookKeepingFiguresOfPeriod(List<Claim> grossClaims, List<UnderwritingInfo> grossUnderwritingInfos, double coveredByReinsurer) {
        super.initBookKeepingFiguresOfPeriod(grossClaims, grossUnderwritingInfos, coveredByReinsurer)
        cededRatiosByEvent.clear()
        grossClaimsAggregatedByEvent.clear()
        for (Claim grossClaim: grossClaims) {
            if (grossClaim.claimType == ClaimType.EVENT || grossClaim.claimType == ClaimType.AGGREGATED_EVENT) {
                Claim mergedClaim = grossClaimsAggregatedByEvent.get(grossClaim.event)
                if (mergedClaim != null) {
                    mergedClaim.plus(grossClaim)
//                    grossClaimsAggregatedByEvent.put(grossClaim.event, mergedClaim)
                }
                else {
                    grossClaimsAggregatedByEvent.put(grossClaim.event, grossClaim.copy())
                }
//                if (remainingCededReserves.get(grossClaim) == null) {
//                    remainingCededReserves.put((ClaimDevelopmentPacket) grossClaim, 0)
//                }
            }
        }

        for (Map.Entry<Event, Claim> aggregateGrossClaim : grossClaimsAggregatedByEvent.entrySet()) {
            Claim aggregateCededClaim = calculateCededClaimAggregatedByEvent(aggregateGrossClaim.value, coveredByReinsurer)
            Claim ratio
            if (aggregateCededClaim instanceof ClaimDevelopmentPacket) {
                ratio = new ClaimDevelopmentPacket()
                ratio.incurred = aggregateGrossClaim.value.incurred == 0 ? 0 : aggregateCededClaim.incurred / aggregateGrossClaim.value.incurred
                ratio.paid = aggregateGrossClaim.value.paid == 0 ? 0 : aggregateCededClaim.paid / aggregateGrossClaim.value.paid
//                ratio.reserved = aggregateGrossClaim.value.reserved == 0 ? 0 : aggregateCededClaim.reserved / aggregateGrossClaim.value.reserved
            }
            else if (aggregateCededClaim instanceof Claim) {
                ratio = new Claim()
                ratio.ultimate = aggregateCededClaim.ultimate / aggregateGrossClaim.value.ultimate
            }
            cededRatiosByEvent.put(aggregateGrossClaim.key, ratio)
        }
    }

    Claim calculateCededClaim(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy()
        cededClaim.scale(0)
        if (grossClaim.claimType.equals(ClaimType.EVENT) || grossClaim.claimType.equals(ClaimType.AGGREGATED_EVENT)) {
            if (grossClaim instanceof ClaimDevelopmentPacket) {
                ClaimDevelopmentPacket ratio = (ClaimDevelopmentPacket) cededRatiosByEvent.get(grossClaim.event)
                println "cCC event ${grossClaim.event}, ratio $ratio (event hashCode ${grossClaim.event.hashCode()}), claim $grossClaim"
                cededClaim.incurred = grossClaim.incurred * ratio.incurred
                cededClaim.paid = grossClaim.paid * ratio.paid
                if (grossClaim.incurred > 0) {
                    cededClaim.reserved = cededClaim.incurred - cededClaim.paid
                    remainingCededReserves.put(grossClaim, cededClaim.reserved)
                }
                else {
                    cededClaim.reserved = remainingCededReserves.get(grossClaim.originalClaim) - cededClaim.paid
                    remainingCededReserves.put(grossClaim.originalClaim, cededClaim.reserved)
                }
                cededClaim.changeInReserves = cededClaim.paid
            }
            else if (grossClaim instanceof Claim) {
                Claim ratio = cededRatiosByEvent.get(grossClaim.event)
                cededClaim.ultimate = grossClaim.ultimate * ratio.ultimate
            }
        }
        return cededClaim
    }

    /**
     * Calculates an aggregate ceded event claim. The maps originalAggregateEventClaimCumulative Ceded/Gross Claim are
     * updated if the claims are of type ClaimDevelopmentPacket.
     */
    Claim calculateCededClaimAggregatedByEvent(Claim grossClaim, double coveredByReinsurer) {
        Claim cededClaim = grossClaim.copy()
        cededClaim.scale(0)
        if (grossClaim instanceof ClaimDevelopmentPacket) {
            // get cumulative gross and ceded claim using the reference of the original claim
            ClaimDevelopmentPacket cumulativeCededClaim = originalAggregateEventClaimCumulativeCededClaim.get(grossClaim.event);
            ClaimDevelopmentPacket cumulativeGrossClaim = originalAggregateEventClaimCumulativeGrossClaim.get(grossClaim.event);
            if (isIncurredPeriod(cumulativeGrossClaim) && !exhausted()) {
                cumulativeCededClaim = (ClaimDevelopmentPacket) grossClaim.copy()
                cumulativeCededClaim.scale(0)
                cumulativeGrossClaim = (ClaimDevelopmentPacket) grossClaim.copy()
                cumulativeGrossClaim.setPaid 0      // set to 0 to avoid double counting as it is added for all periods after the if statement
                cumulativeGrossClaim.setReserved 0
                cumulativeGrossClaim.setChangeInReserves 0
                cededClaim.incurred = calculateCededIncurredValueAndUpdateCumulativeClaims(cumulativeGrossClaim, cumulativeCededClaim, coveredByReinsurer)
//                if (grossClaim.originalClaim == null) {
                    originalAggregateEventClaimCumulativeCededClaim.put(grossClaim.event, cumulativeCededClaim)
                    originalAggregateEventClaimCumulativeGrossClaim.put(grossClaim.event, cumulativeGrossClaim)
//                }
//                else {
//                    originalAggregateEventClaimCumulativeCededClaim.put( grossClaim.originalClaim, cumulativeCededClaim)
//                    originalAggregateEventClaimCumulativeGrossClaim.put((ClaimDevelopmentPacket) grossClaim.originalClaim, cumulativeGrossClaim)
//                }

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
        return cededClaim
    }

    private boolean isIncurredPeriod(ClaimDevelopmentPacket claim) {
        return claim == null
    }

    public void resetMemberInstances() {
        grossClaimsAggregatedByEvent.clear()
        cededRatiosByEvent.clear()
    }
}

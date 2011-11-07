package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;

import java.util.List;

/**
 * This component filters from the incoming claims and underwriting information
 * the packets whose line is listed in parameter parmCoveredLines and provides
 * them in the corresponding out Packetlists.
 * If the parameter contains no line at all, all packets are sent as is to the
 * next component. Packets are not modified.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiCoverAttributeFACReinsuranceContract extends MultiCoverAttributeReinsuranceContract {

    private Boolean parmConsiderFACPercentages = false;

    public void calculateClaims(List<Claim> grossClaims, List<Claim> cededClaims, List<Claim> netClaims, Component origin) {
        if (parmConsiderFACPercentages) {
            for (Claim claim : grossClaims) {
                Claim cededClaim = getCoveredClaim(claim, origin).scale(getCoveredByReinsurer() * claim.getFacShare(parmContractStrategy));
                cededClaims.add(cededClaim);

                Claim claimNet = getNetClaim(claim, cededClaim);
                adjustAttachedExposureInfo(claim, claimNet);
                setClaimReferences(claimNet, claim, origin);
                netClaims.add(claimNet);
            }
        }
        else {
            super.calculateClaims(grossClaims, cededClaims, netClaims, origin);
        }
    }

    public void calculateCededClaims(List<Claim> grossClaims, List<Claim> cededClaims, Component origin) {
        if (parmConsiderFACPercentages) {
            for (Claim claim : grossClaims) {
                cededClaims.add(getCoveredClaim(claim,origin).scale(getCoveredByReinsurer() * claim.getFacShare(parmContractStrategy)));
            }
        }
        else {
            super.calculateCededClaims(grossClaims, cededClaims, origin);
        }
    }

    Claim getNetClaim(Claim grossClaim, Claim cededClaim) {
        if (grossClaim instanceof ClaimDevelopmentLeanPacket) return getNetClaimDLP((ClaimDevelopmentLeanPacket) grossClaim, (ClaimDevelopmentLeanPacket) cededClaim);
        double cededUltimateRatio = cededClaim.getUltimate() / grossClaim.getUltimate();
        double ultimateFactor = grossClaim.getFacShare(parmContractStrategy) - cededUltimateRatio;
        Claim netClaim = grossClaim.copy();
        netClaim.setUltimate(grossClaim.getUltimate() * ultimateFactor);
        if (cededClaim.notNull()) {
            netClaim.addMarker(IReinsuranceContractMarker.class, cededClaim.getReinsuranceContract());
        }
        if (grossClaim.hasExposureInfo()) {
            double coverRatio = netClaim.getUltimate() / grossClaim.getUltimate();
            netClaim.setExposure(grossClaim.getExposure().copy().scale(coverRatio));
        }
        return netClaim;
    }

    ClaimDevelopmentLeanPacket getNetClaimDLP(ClaimDevelopmentLeanPacket grossClaim, ClaimDevelopmentLeanPacket cededClaim) {
        double cededUltimateRatio = grossClaim.getUltimate() > 0 ? cededClaim.getUltimate() / grossClaim.getUltimate() : 0d;
        double ultimateFactor = grossClaim.getFacShare(parmContractStrategy) - cededUltimateRatio;
        double cededPaidRatio = grossClaim.getPaid() > 0 ? cededClaim.getPaid() / grossClaim.getPaid() : 0d;
        double paidFactor = grossClaim.getFacShare(parmContractStrategy) - cededPaidRatio;
        double cededReservedRatio = grossClaim.getReserved() > 0 ? cededClaim.getReserved() / grossClaim.getReserved() : 0d;
        double reservedFactor = grossClaim.getFacShare(parmContractStrategy) - cededReservedRatio;

        ClaimDevelopmentLeanPacket netClaim = (ClaimDevelopmentLeanPacket) grossClaim.copy();
        netClaim.setUltimate(grossClaim.getUltimate() * ultimateFactor);
        netClaim.setPaid(grossClaim.getPaid() * paidFactor);
        netClaim.setReserved(grossClaim.getReserved() * reservedFactor);

        if (grossClaim.hasExposureInfo()) {
            double coverRatio = netClaim.getUltimate() / grossClaim.getUltimate();
            netClaim.setExposure(grossClaim.getExposure().copy().scale(coverRatio));
        }
        return netClaim;
    }

    public Boolean getParmConsiderFACPercentages() {
        return parmConsiderFACPercentages;
    }

    public void setParmConsiderFACPercentages(Boolean parmConsiderFACPercentages) {
        this.parmConsiderFACPercentages = parmConsiderFACPercentages;
    }
}
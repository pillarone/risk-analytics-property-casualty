package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.TrivialRiskAllocatorStrategy;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareAndRetention;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareUtils;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DevelopedTypableFACClaimsGenerator extends TypableClaimsGenerator implements IPerilMarker {

    private PacketList<FacShareAndRetention> inDistributionsByUwInfo = new PacketList<FacShareAndRetention>(FacShareAndRetention.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private double parmPeriodPaymentPortion = 1d;
    private double parmDefaultFacShare = 0d;

    protected void doCalculation() {
        super.doCalculation();
        if (inDistributionsByUwInfo.size() > 0 && !(getParmAssociateExposureInfo() instanceof TrivialRiskAllocatorStrategy)) {
            FacShareAndRetention facShareAndRetention = FacShareUtils.filterFacShares(
                    inDistributionsByUwInfo, (List) getParmUnderwritingInformation().getValuesAsObjects(0, false));
            if (facShareAndRetention != null) {
                for (Claim claim : getOutClaims()) {
                    claim.updateExposureWithFac(facShareAndRetention, parmDefaultFacShare);
                }
            }
            else {
                applyDefaultFacShare();
            }
        }
        else if (!(getParmAssociateExposureInfo() instanceof TrivialRiskAllocatorStrategy)) {
            applyDefaultFacShare();
        }
        for (Claim claim : getOutClaims()) {
            ClaimDevelopmentLeanPacket claimDevelopment = new ClaimDevelopmentLeanPacket(claim);
            claimDevelopment.setIncurred(claim.getUltimate());
            claimDevelopment.setPaid(claim.getUltimate() * parmPeriodPaymentPortion);
            claimDevelopment.setOrigin(this);
            outClaimsLeanDevelopment.add(claimDevelopment);
        }
        getOutClaims().clear();
        getOutClaims().addAll(outClaimsLeanDevelopment);
    }

    private void applyDefaultFacShare() {
        if (parmDefaultFacShare > 0) {
            // no action needed for equal 0 as the default would be applied in this case
            for (Claim claim : getOutClaims()) {
                claim.updateExposureWithFac(parmDefaultFacShare);
            }
        }
    }

    public double getParmPeriodPaymentPortion() {
        return parmPeriodPaymentPortion;
    }

    public void setParmPeriodPaymentPortion(double parmPeriodPaymentPortion) {
        this.parmPeriodPaymentPortion = parmPeriodPaymentPortion;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopment() {
        return outClaimsLeanDevelopment;
    }

    public void setOutClaimsLeanDevelopment(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment) {
        this.outClaimsLeanDevelopment = outClaimsLeanDevelopment;
    }

    public PacketList<FacShareAndRetention> getInDistributionsByUwInfo() {
        return inDistributionsByUwInfo;
    }

    public void setInDistributionsByUwInfo(PacketList<FacShareAndRetention> inDistributionsByUwInfo) {
        this.inDistributionsByUwInfo = inDistributionsByUwInfo;
    }

    public double getParmDefaultFacShare() {
        return parmDefaultFacShare;
    }

    public void setParmDefaultFacShare(double parmDefaultFacShare) {
        this.parmDefaultFacShare = parmDefaultFacShare;
    }
}
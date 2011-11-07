package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.TrivialRiskAllocatorStrategy;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareAndRetention;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareUtils;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","GENERATOR","ATTRITIONAL","SINGLE","EVENT"})
public class DevelopedTypableClaimsGenerator extends TypableClaimsGenerator implements IPerilMarker {

    private PacketList<FacShareAndRetention> inDistributionsByUwInfo = new PacketList<FacShareAndRetention>(FacShareAndRetention.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private double parmPeriodPaymentPortion = 1d;

    protected void doCalculation() {
        super.doCalculation();
        if (inDistributionsByUwInfo.size() > 0 && !(getParmAssociateExposureInfo() instanceof TrivialRiskAllocatorStrategy)) {
            FacShareAndRetention facShareAndRetention = FacShareUtils.filterFacShares(
                    inDistributionsByUwInfo, (List) getParmUnderwritingInformation().getValuesAsObjects(0, false));
            if (facShareAndRetention != null) {
                for (Claim claim : getOutClaims()) {
                    claim.updateExposureWithFac(facShareAndRetention);
                }
            }
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
}
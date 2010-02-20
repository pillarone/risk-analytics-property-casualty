package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.core.packets.PacketList;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DevelopedTypableClaimsGenerator extends TypableClaimsGenerator implements PerilMarker {

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private double parmPeriodPaymentPortion = 1d;

    protected void doCalculation() {
        super.doCalculation();
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
}
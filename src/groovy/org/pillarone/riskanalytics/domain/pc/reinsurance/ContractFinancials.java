package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class ContractFinancials extends Component {

    private PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo> (CededUnderwritingInfo.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim> (Claim.class);
    private PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials =
            new PacketList<ReinsuranceResultWithCommissionPacket> (ReinsuranceResultWithCommissionPacket.class);

    @Override
    protected void doCalculation() {
        ReinsuranceResultWithCommissionPacket result = new ReinsuranceResultWithCommissionPacket();
        CededUnderwritingInfo underwritingInfo = CededUnderwritingInfoUtilities.aggregate(inUnderwritingInfoCeded);
        if (underwritingInfo != null) {
            result.setCededPremium(-underwritingInfo.getPremium());
            result.setCededCommission(-underwritingInfo.getCommission());
        }
        if (inClaimsCeded.size() > 0) {
            result.setCededClaim(ClaimUtilities.aggregateClaims(inClaimsCeded, this).getUltimate());
        }
        outContractFinancials.add(result);
    }

    public PacketList<CededUnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<Claim> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<Claim> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<ReinsuranceResultWithCommissionPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }
}

package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingResult;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AlmResultAggregator extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<Claim> inAlm = new PacketList<Claim>(Claim.class);

    private PacketList<SingleValuePacket> outResult = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<UnderwritingResult> outUnderwritingResult = new PacketList<UnderwritingResult>(UnderwritingResult.class);
    private PacketList<Claim> outAlm = new PacketList<Claim>(Claim.class);

    @Override
    protected void doCalculation() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoUtilities.aggregate(inUnderwritingInfo);
        UnderwritingResult uwResult = new UnderwritingResult();
        if (underwritingInfo != null) {
            uwResult.premium = underwritingInfo.premiumWritten;
            uwResult.commission = underwritingInfo.commission;
        }
        Claim aggregateClaim = ClaimUtilities.aggregateClaims(inClaims, this);
        if (aggregateClaim != null) {
            uwResult.claim = aggregateClaim.getUltimate();
        }
        if (underwritingInfo != null && aggregateClaim != null) {
            uwResult.underwritingResult = uwResult.premium + uwResult.commission - uwResult.claim;
            outUnderwritingResult.add(uwResult);
        }

        Claim alm = ClaimUtilities.aggregateClaims(inAlm, this);
        SingleValuePacket result = new SingleValuePacket();
        if (alm != null) {
            outAlm.add(alm);
            result.setValue(uwResult.getUnderwritingResult() - alm.getUltimate());
        }
        else {
            result.setValue(uwResult.getUnderwritingResult());
        }
        if (underwritingInfo != null && aggregateClaim != null && alm != null) {
            outResult.add(result);
        }
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<Claim> getInAlm() {
        return inAlm;
    }

    public void setInAlm(PacketList<Claim> inAlm) {
        this.inAlm = inAlm;
    }

    public PacketList<SingleValuePacket> getOutResult() {
        return outResult;
    }

    public void setOutResult(PacketList<SingleValuePacket> outResult) {
        this.outResult = outResult;
    }

    public PacketList<UnderwritingResult> getOutUnderwritingResult() {
        return outUnderwritingResult;
    }

    public void setOutUnderwritingResult(PacketList<UnderwritingResult> outUnderwritingResult) {
        this.outUnderwritingResult = outUnderwritingResult;
    }

    public PacketList<Claim> getOutAlm() {
        return outAlm;
    }

    public void setOutAlm(PacketList<Claim> outAlm) {
        this.outAlm = outAlm;
    }
}

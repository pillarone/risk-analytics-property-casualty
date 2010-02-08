package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingResult;
import org.pillarone.riskanalytics.domain.utils.ResultPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AlmResultAggregator extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<Claim> inAlm = new PacketList<Claim>(Claim.class);

    private PacketList<ResultPacket> outTotal = new PacketList<ResultPacket>(ResultPacket.class);
    private PacketList<UnderwritingResult> outUnderwriting = new PacketList<UnderwritingResult>(UnderwritingResult.class);
    private PacketList<ResultPacket> outAlm = new PacketList<ResultPacket>(ResultPacket.class);

    @Override
    protected void doCalculation() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoUtilities.aggregate(inUnderwritingInfo);
        UnderwritingResult uwResult = new UnderwritingResult();
        if (underwritingInfo != null) {
            uwResult.setPremium(underwritingInfo.premiumWritten);
            uwResult.setCommission(underwritingInfo.commission);
        }
        Claim aggregateClaim = ClaimUtilities.aggregateClaims(inClaims, this);
        if (aggregateClaim != null) {
            uwResult.setClaim(aggregateClaim.getUltimate());
        }
        if (underwritingInfo != null && aggregateClaim != null) {
            uwResult.setResult(uwResult.getPremium() + uwResult.getCommission() - uwResult.getClaim());
            outUnderwriting.add(uwResult);
        }

        Claim alm = ClaimUtilities.aggregateClaims(inAlm, this);
        ResultPacket result = new ResultPacket();
        if (alm != null) {
            result.setValue(alm.getUltimate());
            outAlm.add(result);
            result.setValue(uwResult.getResult() - alm.getUltimate());
        }
        else {
            result.setValue(uwResult.getResult());
        }
        if (underwritingInfo != null && aggregateClaim != null && alm != null) {
            outTotal.add(result);
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

    public PacketList<UnderwritingResult> getOutUnderwriting() {
        return outUnderwriting;
    }

    public void setOutUnderwriting(PacketList<UnderwritingResult> outUnderwriting) {
        this.outUnderwriting = outUnderwriting;
    }

    public PacketList<ResultPacket> getOutTotal() {
        return outTotal;
    }

    public void setOutTotal(PacketList<ResultPacket> outTotal) {
        this.outTotal = outTotal;
    }

    public PacketList<ResultPacket> getOutAlm() {
        return outAlm;
    }

    public void setOutAlm(PacketList<ResultPacket> outAlm) {
        this.outAlm = outAlm;
    }
}

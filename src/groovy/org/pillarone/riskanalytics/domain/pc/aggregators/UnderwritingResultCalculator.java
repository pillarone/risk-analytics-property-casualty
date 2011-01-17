package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingResult;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingResultCalculator extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingResult> outUnderwritingResult = new PacketList<UnderwritingResult>(UnderwritingResult.class);

    public void validateWiring() {
        if (!(isReceiverWired(inClaims) && isReceiverWired(inUnderwritingInfo))) {
            throw new IllegalStateException("UnderwritingResultCalculator.missingWiring");
        }
        super.validateWiring();
    }

    protected void doCalculation() {
        UnderwritingInfo underwritingInfo = UnderwritingInfoUtilities.aggregate(inUnderwritingInfo);
        UnderwritingResult uwResult = new UnderwritingResult();
        uwResult.setPremium(underwritingInfo.getPremium());
        uwResult.setCommission(underwritingInfo.getCommission());
        uwResult.setClaim(ClaimUtilities.aggregateClaims(inClaims, this).getUltimate());
        uwResult.setResult(uwResult.getPremium() + uwResult.getCommission() - uwResult.getClaim());
        outUnderwritingResult.add(uwResult);
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

    public PacketList<UnderwritingResult> getOutUnderwritingResult() {
        return outUnderwritingResult;
    }

    public void setOutUnderwritingResult(PacketList<UnderwritingResult> outUnderwritingResult) {
        this.outUnderwritingResult = outUnderwritingResult;
    }
}

package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.constants.Exposure;
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;


/**
 * A claims generator component produce claims and writes them to the channel outClaims.
 * Claims are generated as soon as the doCalculation() method is invoked.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class ClaimsGenerator extends GeneratorCachingComponent {
    private Exposure parmBase = Exposure.ABSOLUTE;

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);

    protected double getScalingFactor() {
        double scalingFactor = 0d;
        if (isOneSenderWired(inUnderwritingInfo) && parmBase != Exposure.ABSOLUTE) {
            for (UnderwritingInfo uwInfo : inUnderwritingInfo) {
                scalingFactor += uwInfo.scaleValue(parmBase);
            }
        } else {
            scalingFactor = 1d;
        }
        return scalingFactor;
    }

    public Exposure getParmBase() {
        return parmBase;
    }

    public void setParmBase(Exposure parmBase) {
        this.parmBase = parmBase;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }


}
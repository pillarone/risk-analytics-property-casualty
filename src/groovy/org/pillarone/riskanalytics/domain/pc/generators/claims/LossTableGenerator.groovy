package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LossTableGenerator extends ClaimsGenerator {

    /** Input channel for how many claims are to be generated            */
    PacketList<Frequency> inClaimCount = new PacketList(Frequency)

    List<Double> parmLossTable = []

    public void validateWiring() {
        if (!maxOneSenderWired(inUnderwritingInfo)) {
            throw new IllegalStateException("Wiring error: More than one underwriting information source is wired!")
        }
        super.validateWiring();
    }

    public void validateParameterization() {
        if (!parmBase.equals(Exposure.ABSOLUTE) && !isReceiverWired(inUnderwritingInfo)) {
            throw new IllegalStateException("As no underwriting information is provided, only 'absolute' is allowed as base.")
        }
        super.validateParameterization();
    }

    public void doCalculation() {
        //todo (sku): add warning if more than one uw info is received
        double scalingFactor = isOneSenderWired(inUnderwritingInfo) ? inUnderwritingInfo[0].scaleValue(parmBase) : 1d
        int numberOfClaims = parmLossTable.size()

        for (frequency in inClaimCount) {
            for (int i = 0; i < frequency.value; i++) {
                outClaims << new Claim(
                    origin: this,
                    claimType: ClaimType.SINGLE,
                    value: parmLossTable[(i % numberOfClaims)] * scalingFactor)
            }
        }
    }
}
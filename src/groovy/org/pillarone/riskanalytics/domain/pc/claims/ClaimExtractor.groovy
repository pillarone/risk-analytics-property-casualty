package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType

/**
 * @author Michael-Noe (at) web (dot) de
 */

class ClaimExtractor extends Component {
    PacketList<Claim> inClaims = new PacketList(Claim)
    PacketList<Claim> outClaims = new PacketList(Claim)
    PacketList<Claim> outClaimsSingle = new PacketList(Claim)
    PacketList<Claim> outClaimsAttritional = new PacketList(Claim)

    public void doCalculation() {
        for (Claim claim: inClaims) {
            outClaims << claim
            switch (claim.claimType) {
                case ClaimType.SINGLE:
                    outClaimsSingle << claim
                    break
                case ClaimType.ATTRITIONAL:
                    outClaimsAttritional << claim
                    break
                default:
                    break
            }
        }
    }
}



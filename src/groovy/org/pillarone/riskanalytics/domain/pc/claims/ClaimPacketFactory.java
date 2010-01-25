package org.pillarone.riskanalytics.domain.pc.claims;

/**
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): add a Stack<Claim> allowing to recycle claim packets
public class ClaimPacketFactory {

    private static ClaimPacketFactory instance = null;


    private ClaimPacketFactory() {
    }

    public static ClaimPacketFactory getInstance() {
        if (instance == null) {
            instance = new ClaimPacketFactory();
        }
        return instance;
    }

    public static Claim createPacket() {
        Claim claim = new Claim();
        claim.setOriginalClaim(claim);
        return claim;
    }
}
package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimDevelopmentLeanPacketFactory {

    private static ClaimDevelopmentLeanPacketFactory instance = null;


    private ClaimDevelopmentLeanPacketFactory() {
    }

    public static ClaimDevelopmentLeanPacketFactory getInstance() {
        if (instance == null) {
            instance = new ClaimDevelopmentLeanPacketFactory();
        }
        return instance;
    }

    public static ClaimDevelopmentLeanPacket createPacket() {
        return new ClaimDevelopmentLeanPacket();
    }

    public static ClaimDevelopmentLeanPacket copy(ClaimDevelopmentLeanPacket claim) {
        return (ClaimDevelopmentLeanPacket) claim.copy();
    }
}
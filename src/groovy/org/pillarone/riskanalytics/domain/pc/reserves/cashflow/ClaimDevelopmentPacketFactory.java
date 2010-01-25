package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimDevelopmentPacketFactory {

    private static ClaimDevelopmentPacketFactory instance = null;


    private ClaimDevelopmentPacketFactory() {
    }

    public static ClaimDevelopmentPacketFactory getInstance() {
        if (instance == null) {
            instance = new ClaimDevelopmentPacketFactory();
        }
        return instance;
    }

    public static ClaimDevelopmentPacket createPacket() {
        return new ClaimDevelopmentPacket();
    }

    public static ClaimDevelopmentPacket copy(ClaimDevelopmentPacket claim) {
        return (ClaimDevelopmentPacket) claim.copy();
    }
}
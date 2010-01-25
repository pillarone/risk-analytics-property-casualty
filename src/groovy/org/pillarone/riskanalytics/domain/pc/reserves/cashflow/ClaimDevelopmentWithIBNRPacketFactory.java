package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimDevelopmentWithIBNRPacketFactory {

    private static ClaimDevelopmentWithIBNRPacketFactory instance = null;


    private ClaimDevelopmentWithIBNRPacketFactory() {
    }

    public static ClaimDevelopmentWithIBNRPacketFactory getInstance() {
        if (instance == null) {
            instance = new ClaimDevelopmentWithIBNRPacketFactory();
        }
        return instance;
    }

    public static ClaimDevelopmentWithIBNRPacket createPacket() {
        return new ClaimDevelopmentWithIBNRPacket();
    }

    public static ClaimDevelopmentWithIBNRPacket copy(ClaimDevelopmentWithIBNRPacket claim) {
        return (ClaimDevelopmentWithIBNRPacket) claim.copy();
    }
}
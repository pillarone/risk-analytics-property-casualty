package org.pillarone.riskanalytics.domain.pc.underwriting;

/**
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): add a Stack<UnderwritingInfo> allowing to recycle UnderwritingInfo packets
public class UnderwritingInfoPacketFactory {

    private static UnderwritingInfoPacketFactory instance = null;


    private UnderwritingInfoPacketFactory() {
    }

    public static UnderwritingInfoPacketFactory getInstance() {
        if (instance == null) {
            instance = new UnderwritingInfoPacketFactory();
        }
        return instance;
    }

    public static UnderwritingInfo createPacket() {
        return new UnderwritingInfo();
    }

    public static UnderwritingInfo copy(UnderwritingInfo underwritingInfo) {
        return (UnderwritingInfo) underwritingInfo.copy();
    }
}
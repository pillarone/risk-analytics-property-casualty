package org.pillarone.riskanalytics.domain.pc.underwriting;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class CededUnderwritingInfoPacketFactory {

    private static CededUnderwritingInfoPacketFactory instance = null;


    private CededUnderwritingInfoPacketFactory() {
    }

    public static CededUnderwritingInfoPacketFactory getInstance() {
        if (instance == null) {
            instance = new CededUnderwritingInfoPacketFactory();
        }
        return instance;
    }

    public static CededUnderwritingInfo createPacket() {
        return new CededUnderwritingInfo();
    }

    public static CededUnderwritingInfo copy(UnderwritingInfo underwritingInfo) {
        if (underwritingInfo instanceof CededUnderwritingInfo)
            return ((CededUnderwritingInfo) underwritingInfo).copy();
        return underwritingInfo.copyToSubclass();
    }
}

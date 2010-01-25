package org.pillarone.riskanalytics.domain.pc.generators.frequency;

/**
 *
 *  @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): add a Stack<Claim> allowing to recycle claim packets
public class FrequencyPacketFactory {

    private static FrequencyPacketFactory instance = null;


    private FrequencyPacketFactory() {
    }

    public static FrequencyPacketFactory getInstance() {
        if (instance == null) {
            instance = new FrequencyPacketFactory();
        }
        return instance;
    }

    public static Frequency createPacket() {
        return new Frequency();
    }

    public static Frequency copy(Frequency frequency) {
        return (Frequency) frequency.copy();
    }
}
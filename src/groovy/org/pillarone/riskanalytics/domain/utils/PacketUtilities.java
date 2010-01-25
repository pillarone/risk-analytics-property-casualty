package org.pillarone.riskanalytics.domain.utils;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.core.packets.Packet;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PacketUtilities {

    public static <T extends SingleValuePacket> double sum(List<T> packets) {
        double sum = 0d;
        for (T packet : packets) {
            sum += packet.value;
        }
        return sum;
    }

    public static double sumClaims(List<Claim> claims) {
        double sum = 0d;
        for (Claim claim : claims) {
            sum += claim.getUltimate();
        }
        return sum;
    }

    // todo(sku): use dko if there is a more general solution possible
    public static boolean sameContent(Packet packet1, Packet packet2) {
        return (equals(packet1.origin, packet2.origin)
            && equals(packet1.sender, packet2.sender)
            && equals(packet1.senderChannelName, packet2.senderChannelName));
    }

    public static boolean equals(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        } else if (first == null || second == null) {
            return false;
        } else {
            return first.equals(second);
        }

    }
}

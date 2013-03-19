package org.pillarone.riskanalytics.domain.utils;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pillarone.riskanalytics.core.components.IComponentMarker;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class MarkerKeyPair {

    IComponentMarker marker1;
    IComponentMarker marker2;
    IComponentMarker marker3;
    Integer number;

    public MarkerKeyPair(IComponentMarker firstMarker, IComponentMarker secondMarker) {
        marker1 = firstMarker;
        marker2 = secondMarker;
        number = 2;
    }

    public MarkerKeyPair(IComponentMarker firstMarker, IComponentMarker secondMarker, IComponentMarker thirdMarker) {
        marker1 = firstMarker;
        marker2 = secondMarker;
        marker3 = thirdMarker;
        number = 3;
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(marker1);
        hashCodeBuilder.append(marker2);
        if (number == 3) {
            hashCodeBuilder.append(marker3);
        }
        return hashCodeBuilder.toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MarkerKeyPair) {
            if (number == 3) {
                return ((MarkerKeyPair) obj).getFirst().equals(marker1)
                        && ((MarkerKeyPair) obj).getSecond().equals(marker2)
                        && ((MarkerKeyPair) obj).getThird().equals(marker3)
                        && ((MarkerKeyPair) obj).getNumber().equals(number);
            }
            else {
                return ((MarkerKeyPair) obj).getFirst().equals(marker1)
                        && ((MarkerKeyPair) obj).getSecond().equals(marker2)
                        && ((MarkerKeyPair) obj).getNumber().equals(number);
            }
        } else {
            return false;
        }
    }

    public IComponentMarker getFirst() {
        return marker1;
    }

    public IComponentMarker getSecond() {
        return marker2;
    }

    public IComponentMarker getThird() {
        return marker3;
    }

    public Integer getNumber() {
        return number;
    }
}

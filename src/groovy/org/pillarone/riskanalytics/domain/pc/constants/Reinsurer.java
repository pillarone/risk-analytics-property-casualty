package org.pillarone.riskanalytics.domain.pc.constants;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum Reinsurer {
    MERCURY_RE, VENUS_RE, EARTH_RE, MARS_RE, JUPITER_RE, SATURN_RE, URANUS_RE, NEPTUN_RE;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
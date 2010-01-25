package org.pillarone.riskanalytics.domain.assets;

import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class YieldModellingChoices extends Packet {

    public TermStructureType yieldCurveType;

    public TermStructureType getYieldCurveType() {
        return yieldCurveType;
    }

    public void setYieldCurveType(TermStructureType yieldCurveType) {
        this.yieldCurveType = yieldCurveType;
    }
}
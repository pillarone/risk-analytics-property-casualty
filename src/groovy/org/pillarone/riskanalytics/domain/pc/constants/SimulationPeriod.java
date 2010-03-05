package org.pillarone.riskanalytics.domain.pc.constants;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.utils.DateTimeUtilities;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum SimulationPeriod {
    SIMULATION_PERIOD, ANNUALLY, TERM, QUARTERLY, MONTHLY;

    public static Period asPeriod(SimulationPeriod period, PeriodScope periodScope) {
        switch (period) {
            case SIMULATION_PERIOD:
                return DateTimeUtilities.simulationPeriodLength(periodScope);
            case ANNUALLY:
                return new Period(0, 12, 0,0,0,0,0,0, PeriodType.months());
            case TERM:
                return new Period(0, 6, 0,0,0,0,0,0, PeriodType.months());
            case QUARTERLY:
                return new Period(0, 3, 0,0,0,0,0,0, PeriodType.months());
            case MONTHLY:
                return new Period(0, 1, 0,0,0,0,0,0, PeriodType.months());
            default:
                throw new IllegalArgumentException(period + " not implemented.");
        }
    }
}

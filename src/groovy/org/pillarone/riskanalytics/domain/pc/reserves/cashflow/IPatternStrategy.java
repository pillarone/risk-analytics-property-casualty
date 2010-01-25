package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IPatternStrategy {
    List<Double> getPatternValues();
}
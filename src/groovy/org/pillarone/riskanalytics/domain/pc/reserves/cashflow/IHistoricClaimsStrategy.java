package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IHistoricClaimsStrategy extends IParameterObject {
    /**
     * @return map with the development period as key and the diagonal value as value
     */
    Map<Integer, Double> getDiagonalValues();
}
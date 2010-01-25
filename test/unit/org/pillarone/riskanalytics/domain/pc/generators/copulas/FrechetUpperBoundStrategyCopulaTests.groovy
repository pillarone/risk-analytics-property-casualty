package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FrechetUpperBoundStrategyCopulaTests extends GroovyTestCase {

    static Copula getCopula() {
        return new LobCopula(
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(
                        LobCopulaType.FRECHETUPPERBOUND, ["targets": new SimpleMultiDimensionalParameter(["Fire", "Hull", "Legal"])]))
    }

    void testDoCalculation() {
        Copula copula = getCopula()
        copula.inNumber << new Frequency(value: 5)
        copula.doCalculation()
        assertEquals "corresponding severity size", copula.parmCopulaStrategy.getTargetNames().size(), copula.outProbabilities[0].probabilities.size()
    }

    void testSameSeverity() {
        Copula copula = getCopula()
        copula.inNumber << new Frequency(value: 5)
        copula.doCalculation()
        double firstSeverity = copula.outProbabilities[0].probabilities[0]
        for (double severity: copula.outProbabilities[0].probabilities) {
            assertEquals "equal severities", severity, firstSeverity
        }
    }
}
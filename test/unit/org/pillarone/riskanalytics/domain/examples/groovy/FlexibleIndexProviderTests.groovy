package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FlexibleIndexProviderTests extends GroovyTestCase {

    void testUsageAbsoluteIndex() {
        FlexibleIndexProvider index = new FlexibleIndexProvider()
        index.periodScope = new PeriodScope()
        index.doCalculation()
        assertEquals 'default absolute index', 1, index.outIndex[0].value
        index.reset()

        index.parmIndex = IndexType.getStrategy(IndexType.ABSOLUTE, ['index': 1.236])
        index.doCalculation()
        assertEquals 'modified absolute index', 1.236, index.outIndex[0].value
    }

    void testUsageRelativeIndex() {
        FlexibleIndexProvider index = new FlexibleIndexProvider(parmIndex : IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ['changeIndex': 0.5]))
        index.periodScope = new PeriodScope()
        index.doCalculation()
        assertEquals 'relative index period 0', 0.5, index.outIndex[0].value
        index.reset()

        index.parmIndex = IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ['changeIndex': 3])
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals 'relative index period 1', 1.5, index.outIndex[0].value
        index.reset()

        index.periodScope.reset()
        index.doCalculation()
        assertEquals 'relative index period 0/1', 3.0, index.outIndex[0].value
    }

    void testMixedMode() {
        FlexibleIndexProvider index = new FlexibleIndexProvider(parmIndex : IndexType.getStrategy(IndexType.ABSOLUTE, ['index': 1.06]))
        index.periodScope = new PeriodScope()
        index.doCalculation()
        assertEquals 'index period 0', 1.06, index.outIndex[0].value
        index.reset()

        index.parmIndex = IndexType.getStrategy(IndexType.ABSOLUTE, ['index': 1.04])
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals 'index period 1', 1.04, index.outIndex[0].value
        index.reset()

        index.parmIndex = IndexType.getStrategy(IndexType.RELATIVEPRIORPERIOD, ['changeIndex': 0.98])
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals 'index period 2', 0.98 * 1.04, index.outIndex[0].value
        index.reset()

        index.parmIndex = IndexType.getStrategy(IndexType.ABSOLUTE, ['index': 1.08])
        index.periodScope.prepareNextPeriod()
        index.doCalculation()
        assertEquals 'index period 1', 1.08, index.outIndex[0].value
    }
}

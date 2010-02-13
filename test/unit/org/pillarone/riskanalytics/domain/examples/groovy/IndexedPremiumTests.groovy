package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.util.TestProbe

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexedPremiumTests extends GroovyTestCase {

    void testUsage() {
        IndexedPremium indexedPremium = new IndexedPremium()
        indexedPremium.subIndexProvider.parmIndex = 1.02
        indexedPremium.subPremiumCalculation.parmNumberOfPolicy = 100
        indexedPremium.subPremiumCalculation.parmPricePerPolicy = 10
        indexedPremium.internalWiring()

        List premium = new TestProbe(indexedPremium, 'outPremium').result
        indexedPremium.start()

        assertEquals 'premium, single index', 1020, premium[0].value, 1E-10
    }
}

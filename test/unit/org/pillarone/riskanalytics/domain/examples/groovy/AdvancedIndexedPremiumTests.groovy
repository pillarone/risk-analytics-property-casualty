package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

class AdvancedIndexedPremiumTests extends GroovyTestCase {

    // have to be defined as instance variables. Wiring.utils(WireCategory) won't work with local variables.
    IndexProvider globalIndex
    AdvancedIndexedPremium indexedPremium

    void testUsage() {
        globalIndex = new IndexProvider()
        globalIndex.parmIndex = 1.01

        indexedPremium = new AdvancedIndexedPremium()
        indexedPremium.subIndexProvider.parmIndex = 1.02
        indexedPremium.subPremiumCalculation.parmNumberOfPolicy = 100
        indexedPremium.subPremiumCalculation.parmPricePerPolicy = 10

        WiringUtils.use(WireCategory) {
            indexedPremium.inIndex = globalIndex.outIndex
        }
        indexedPremium.internalWiring()

        List premium = new TestProbe(indexedPremium, 'outPremium').result
        globalIndex.start()

        //todo(sku): fix, PMO-1429
//        assertEquals 'premium, single index', 1030.2, premium[0].value, 1E-10
    }
}
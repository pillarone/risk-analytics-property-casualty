package org.pillarone.riskanalytics.domain.pc.utils

import umontreal.iro.lecuyer.stat.TallyStore
import org.pillarone.riskanalytics.domain.utils.Statistic

class StatisticTests extends GroovyTestCase {

    void testQuantile() {
        TallyStore tallyStore = new TallyStore()
        tallyStore.add 1
        tallyStore.add 2
        tallyStore.add 3
        tallyStore.add 4

        Statistic statistic = new Statistic(tally: tallyStore)

        assertEquals 1.3, statistic.calculateQuantile(0.1), 0.0001
        assertEquals 2.5, statistic.calculateQuantile(0.5), 0.0001
        assertEquals 3.1, statistic.calculateQuantile(0.7), 0.0001
        
    }

    void testCenteredQuantile() {
        TallyStore tallyStore = new TallyStore()
        tallyStore.add 1
        tallyStore.add 2
        tallyStore.add 3
        tallyStore.add 4

        Statistic statistic = new Statistic(tally: tallyStore)

        assertEquals 1.3 - 2.5, statistic.calculateCenteredQuantile(0.1), 0.0001
        assertEquals 2.5 - 2.5, statistic.calculateCenteredQuantile(0.5), 0.0001
        assertEquals 3.1 - 2.5, statistic.calculateCenteredQuantile(0.7), 0.0001

    }

}
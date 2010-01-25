package org.pillarone.riskanalytics.domain.pc.reinsurance

import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class FiniteReTests extends GroovyTestCase {

    static FiniteRe getFiniteRe() {
        FiniteRe eab = new FiniteRe(parmPremium: 3000000, parmFractionExperienceAccount: 0.5)
        SimulationScope simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()));
        eab.periodStore = new PeriodStore(simulationScope.iterationScope.periodScope)
        return eab
    }

    void testUsage() {
        FiniteRe eab = getFiniteRe()
        eab.inClaims << new Claim(value: 0)

        eab.doCalculation()

        assertEquals 'one finiteRe packet, P0', 1, eab.outExperienceAccount.size()
        assertEquals 'ea premium, P0', 1500000, eab.outExperienceAccount[0].premium
        assertEquals 'ea claim, P0', 0, eab.outExperienceAccount[0].claim
        assertEquals 'ea balance, P0', 1500000, eab.outExperienceAccount[0].balance

        assertEquals 'one reinsurance packet, P0', 1, eab.outReinsurance.size()
        assertEquals 'r/i premium, P0', 1500000, eab.outReinsurance[0].premium
        assertEquals 'r/i claim, P0', 0, eab.outReinsurance[0].claim
        assertEquals 'r/i result, P0', 1500000, eab.outReinsurance[0].result

        eab.reset()
        eab.periodStore.periodScope.currentPeriod++

        eab.inClaims << new Claim(value: 1000000)
        eab.doCalculation()

        assertEquals 'one finiteRe packet, P1', 1, eab.outExperienceAccount.size()
        assertEquals 'ea premium, P1', 1500000, eab.outExperienceAccount[0].premium
        assertEquals 'ea claim, P1',   1000000, eab.outExperienceAccount[0].claim
        assertEquals 'ea balance, P1', 2000000, eab.outExperienceAccount[0].balance

        assertEquals 'one reinsurance packet, P1', 1, eab.outReinsurance.size()
        assertEquals 'r/i premium, P1', 1500000, eab.outReinsurance[0].premium
        assertEquals 'r/i claim, P1', 0, eab.outReinsurance[0].claim
        assertEquals 'r/i result, P1', 3000000, eab.outReinsurance[0].result

        eab.reset()
        eab.periodStore.periodScope.currentPeriod++

        eab.inClaims << new Claim(value: 3000000)
        eab.doCalculation()

        assertEquals 'one finiteRe packet, P2', 1, eab.outExperienceAccount.size()
        assertEquals 'ea premium, P2', 1500000, eab.outExperienceAccount[0].premium
        assertEquals 'ea claim, P2',   3000000, eab.outExperienceAccount[0].claim
        assertEquals 'ea balance, P2',  500000, eab.outExperienceAccount[0].balance

        assertEquals 'one reinsurance packet, P2', 1, eab.outReinsurance.size()
        assertEquals 'r/i premium, P2', 1500000, eab.outReinsurance[0].premium
        assertEquals 'r/i claim, P2',   0, eab.outReinsurance[0].claim
        assertEquals 'r/i result, P2',  4500000, eab.outReinsurance[0].result

        eab.reset()
        eab.periodStore.periodScope.currentPeriod++

        eab.inClaims << new Claim(value: 3000000)
        eab.doCalculation()

        assertEquals 'one finiteRe packet, P3', 1, eab.outExperienceAccount.size()
        assertEquals 'ea premium, P3', 1500000, eab.outExperienceAccount[0].premium
        assertEquals 'ea claim, P3',   2000000, eab.outExperienceAccount[0].claim
        assertEquals 'ea balance, P3', 0, eab.outExperienceAccount[0].balance

        assertEquals 'one reinsurance packet, P3', 1, eab.outReinsurance.size()
        assertEquals 'r/i premium, P3', 1500000, eab.outReinsurance[0].premium
        assertEquals 'r/i claim, P3',   1000000, eab.outReinsurance[0].claim
        assertEquals 'r/i result, P3',  5000000, eab.outReinsurance[0].result

        eab.reset()
        eab.periodStore.periodScope.currentPeriod++

        eab.inClaims << new Claim(value: 1000000)
        eab.doCalculation()

        assertEquals 'one finiteRe packet, P4', 1, eab.outExperienceAccount.size()
        assertEquals 'ea premium, P4', 1500000, eab.outExperienceAccount[0].premium
        assertEquals 'ea claim, P4',   1000000, eab.outExperienceAccount[0].claim
        assertEquals 'ea balance, P4',  500000, eab.outExperienceAccount[0].balance

        assertEquals 'one reinsurance packet, P4', 1, eab.outReinsurance.size()
        assertEquals 'r/i premium, P4', 1500000, eab.outReinsurance[0].premium
        assertEquals 'r/i claim, P4',   0, eab.outReinsurance[0].claim
        assertEquals 'r/i result, P4',  6500000, eab.outReinsurance[0].result
    }
}
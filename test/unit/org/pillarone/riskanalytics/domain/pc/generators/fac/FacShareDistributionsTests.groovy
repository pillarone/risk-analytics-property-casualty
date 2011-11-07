package org.pillarone.riskanalytics.domain.pc.generators.fac

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.IterationStore
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskBands
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.util.MathUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FacShareDistributionsTests extends GroovyTestCase {

    FacShareDistributions facShareDistributions
    RiskBands motor
    RiskBands mtpl

    void setUp() {
        IterationScope iterationScope = new IterationScope(periodScope: new PeriodScope())
        iterationScope.prepareNextIteration()
        motor = new RiskBands(name: 'motor')
        mtpl = new RiskBands(name: 'mtpl')
        facShareDistributions = new FacShareDistributions()
        facShareDistributions.parmLinkedUnderwritingInfo = new ConstrainedString(IUnderwritingInfoMarker, 'motor')
        facShareDistributions.parmLinkedUnderwritingInfo.selectedComponent = motor
        facShareDistributions.iterationStore = new IterationStore(iterationScope)
        MathUtils.initRandomStreamBase(1234)
    }

    void testUsage() {
        UnderwritingInfo underwritingInfo1000 = new UnderwritingInfo(maxSumInsured: 1000, origin: motor)
        UnderwritingInfo underwritingInfo1200 = new UnderwritingInfo(maxSumInsured: 1200, origin: motor)
        facShareDistributions.inUnderwritingInfo << underwritingInfo1000 << underwritingInfo1200
        facShareDistributions.parmAllocation = new TableMultiDimensionalParameter(
                [[1000d, 1000d, 1000d, 1200d, 1200d, 1200d],
                        [1d, 1d, 2d, 3d, 4d, 5d],
                        [0.2d, 0.3, 0.35, 0.1, 0.4, 0.9],
                        [0.21d, 0.31, 0.36, 0.11, 0.41, 0.91],
                        [0.1d, 0.2, 0.3, 0.2, 0.3, 0.7]],
                ['Max Sum Insured', 'Count of Policies', 'Quota Share %', 'Surplus %', 'Retention %']
        )
        facShareDistributions.doCalculation()

        assertEquals 'one packet', 1, facShareDistributions.outDistributionsByUwInfo.size()
        FacShareAndRetention facShareAndRetention = facShareDistributions.outDistributionsByUwInfo[0]

        Set shares = new HashSet()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1000) }
        assertEquals 'quota shares for uw1', [0.2d, 0.3, 0.35], shares.toList().toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1200) }
        assertEquals 'quota shares for uw2', [0.1d, 0.4, 0.9], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1000) }
        assertEquals 'surplus shares for uw1', [0.21d, 0.31, 0.36], shares.toList().sort()

        shares.clear()
        20.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1200) }
        assertEquals 'surplus shares for uw2', [0.11d, 0.41, 0.91], shares.toList().sort()
    }

    void testMissingLinkAtEnd() {
        UnderwritingInfo underwritingInfo1000 = new UnderwritingInfo(maxSumInsured: 1000, origin: motor)
        UnderwritingInfo underwritingInfo1200 = new UnderwritingInfo(maxSumInsured: 1200, origin: motor)
        UnderwritingInfo underwritingInfo1400 = new UnderwritingInfo(maxSumInsured: 1400, origin: motor)
        facShareDistributions.inUnderwritingInfo << underwritingInfo1000 << underwritingInfo1200 << underwritingInfo1400
        facShareDistributions.parmAllocation = new TableMultiDimensionalParameter(
                [[1000d, 1000d, 1000d, 1200d, 1200d, 1200d],
                        [1d, 1d, 2d, 3d, 4d, 5d],
                        [0.2d, 0.3, 0.35, 0.1, 0.4, 0.9],
                        [0.21d, 0.31, 0.36, 0.11, 0.41, 0.91],
                        [0.1d, 0.2, 0.3, 0.2, 0.3, 0.7]],
                ['Max Sum Insured', 'Count of Policies', 'Quota Share %', 'Surplus %', 'Retention %']
        )
        facShareDistributions.doCalculation()

        assertEquals 'one packet', 1, facShareDistributions.outDistributionsByUwInfo.size()
        FacShareAndRetention facShareAndRetention = facShareDistributions.outDistributionsByUwInfo[0]

        Set shares = new HashSet()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1400) }
        assertEquals 'surplus shares for uw1400', [0d], shares.toList().sort()
    }

    void testMissingLinkInBetween() {
        UnderwritingInfo underwritingInfo1000 = new UnderwritingInfo(maxSumInsured: 1000, origin: motor)
        UnderwritingInfo underwritingInfo1200 = new UnderwritingInfo(maxSumInsured: 1200, origin: motor)
        UnderwritingInfo underwritingInfo1400 = new UnderwritingInfo(maxSumInsured: 1400, origin: motor)
        facShareDistributions.inUnderwritingInfo << underwritingInfo1000 << underwritingInfo1200 << underwritingInfo1400
        facShareDistributions.parmAllocation = new TableMultiDimensionalParameter(
                [[1000d, 1000d, 1000d, 1400d, 1400d, 1400d],
                        [1d, 1d, 2d, 3d, 4d, 5d],
                        [0.2d, 0.3, 0.35, 0.1, 0.4, 0.9],
                        [0.21d, 0.31, 0.36, 0.11, 0.41, 0.91],
                        [0.1d, 0.2, 0.3, 0.2, 0.3, 0.7]],
                ['Max Sum Insured', 'Count of Policies', 'Quota Share %', 'Surplus %', 'Retention %']
        )
        facShareDistributions.doCalculation()

        assertEquals 'one packet', 1, facShareDistributions.outDistributionsByUwInfo.size()
        FacShareAndRetention facShareAndRetention = facShareDistributions.outDistributionsByUwInfo[0]

        Set shares = new HashSet()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1000) }
        assertEquals 'quota shares for uw1', [0.2d, 0.3, 0.35], shares.toList().toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1000) }
        assertEquals 'surplus shares for uw1', [0.21d, 0.31, 0.36], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1200) }
        assertEquals 'surplus shares for uw1400', [0d], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1200) }
        assertEquals 'surplus shares for uw1400', [0d], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1400) }
        assertEquals 'surplus shares for uw2', [0.11d, 0.41, 0.91], shares.toList().sort()

        shares.clear()
        20.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1400) }
        assertEquals 'retention for uw1', [0.1d, 0.4d, 0.9d], shares.toList().sort()
    }

    void testOnePercentageLineOnly() {
        UnderwritingInfo underwritingInfo1000 = new UnderwritingInfo(maxSumInsured: 1000, origin: motor)
        UnderwritingInfo underwritingInfo1200 = new UnderwritingInfo(maxSumInsured: 1200, origin: motor)
        UnderwritingInfo underwritingInfo1400 = new UnderwritingInfo(maxSumInsured: 1400, origin: motor)
        facShareDistributions.inUnderwritingInfo << underwritingInfo1000 << underwritingInfo1200 << underwritingInfo1400
        facShareDistributions.parmAllocation = new TableMultiDimensionalParameter(
                [[1000d],
                        [1d],
                        [0.2d],
                        [0.21d],
                        [0.1d]],
                ['Max Sum Insured', 'Count of Policies', 'Quota Share %', 'Surplus %', 'Retention %']
        )
        facShareDistributions.doCalculation()

        assertEquals 'one packet', 1, facShareDistributions.outDistributionsByUwInfo.size()
        FacShareAndRetention facShareAndRetention = facShareDistributions.outDistributionsByUwInfo[0]

        Set shares = new HashSet()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1000) }
        assertEquals 'quota shares for uw1', [0.2d], shares.toList().toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1000) }
        assertEquals 'surplus shares for uw1', [0.21d], shares.toList().sort()
    }

    void testUwInfoFilter() {
        UnderwritingInfo underwritingInfo1000 = new UnderwritingInfo(maxSumInsured: 1000, origin: motor)
        UnderwritingInfo underwritingInfo1200 = new UnderwritingInfo(maxSumInsured: 1200, origin: mtpl)
        facShareDistributions.inUnderwritingInfo << underwritingInfo1000 << underwritingInfo1200
        facShareDistributions.parmAllocation = new TableMultiDimensionalParameter(
                [[1000d, 1000d, 1000d, 1200d, 1200d, 1200d],
                        [1d, 1d, 2d, 3d, 4d, 5d],
                        [0.2d, 0.3, 0.35, 0.1, 0.4, 0.9],
                        [0.21d, 0.31, 0.36, 0.11, 0.41, 0.91],
                        [0.1d, 0.2, 0.3, 0.2, 0.3, 0.7]],
                ['Max Sum Insured', 'Count of Policies', 'Quota Share %', 'Surplus %', 'Retention %']
        )
        facShareDistributions.doCalculation()

        assertEquals 'one packet', 1, facShareDistributions.outDistributionsByUwInfo.size()
        FacShareAndRetention facShareAndRetention = facShareDistributions.outDistributionsByUwInfo[0]

        Set shares = new HashSet()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1000) }
        assertEquals 'quota shares for uw1', [0.2d, 0.3, 0.35], shares.toList().toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getQuotaShare(underwritingInfo1200) }
        assertEquals 'quota shares for uw2', [0d], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1000) }
        assertEquals 'surplus shares for uw1', [0.21d, 0.31, 0.36], shares.toList().sort()

        shares.clear()
        10.times { shares.add facShareAndRetention.getSurplusShare(underwritingInfo1200) }
        assertEquals 'surplus shares for uw2', [0d], shares.toList().sort()
    }
}

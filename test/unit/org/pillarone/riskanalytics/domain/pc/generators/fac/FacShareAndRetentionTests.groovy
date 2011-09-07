package org.pillarone.riskanalytics.domain.pc.generators.fac

import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.ConstantDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FacShareAndRetentionTests extends GroovyTestCase {

    void testUsage() {
        UnderwritingInfo underwritingInfo1 = new UnderwritingInfo()
        UnderwritingInfo underwritingInfo2 = new UnderwritingInfo()

        RandomDistribution constantQuotaShareDistribution05 = new RandomDistribution()
        constantQuotaShareDistribution05.distribution = new ConstantDistribution(0.5)
        RandomDistribution constantQuotaShareDistribution015 = new RandomDistribution()
        constantQuotaShareDistribution015.distribution = new ConstantDistribution(0.15)

        RandomDistribution constantSurplusShareDistribution06 = new RandomDistribution()
        constantSurplusShareDistribution06.distribution = new ConstantDistribution(0.6)
        RandomDistribution constantSurplusShareDistribution017 = new RandomDistribution()
        constantSurplusShareDistribution017.distribution = new ConstantDistribution(0.17)

        RandomDistribution constantRetentionDistribution02 = new RandomDistribution()
        constantRetentionDistribution02.distribution = new ConstantDistribution(0.2)
        RandomDistribution constantRetentionDistribution022 = new RandomDistribution()
        constantRetentionDistribution022.distribution = new ConstantDistribution(0.22)

        FacShareAndRetention facShareAndRetention = new FacShareAndRetention()
        facShareAndRetention.add(underwritingInfo1, constantQuotaShareDistribution05, constantSurplusShareDistribution06, constantRetentionDistribution02)
        facShareAndRetention.add(underwritingInfo2, constantQuotaShareDistribution015, constantSurplusShareDistribution017, constantRetentionDistribution022)
        assertEquals 'quota share of uw1', 0.5, facShareAndRetention.getQuotaShare(underwritingInfo1)
        assertEquals 'surplus share of uw1', 0.6, facShareAndRetention.getSurplusShare(underwritingInfo1)
        assertEquals 'retention of uw1', 0.2, facShareAndRetention.getRetention(underwritingInfo1)
        assertEquals 'quota share of uw2', 0.15, facShareAndRetention.getQuotaShare(underwritingInfo2)
        assertEquals 'surplus of uw2', 0.17, facShareAndRetention.getSurplusShare(underwritingInfo2)
        assertEquals 'retention of uw2', 0.22, facShareAndRetention.getRetention(underwritingInfo2)
    }
}

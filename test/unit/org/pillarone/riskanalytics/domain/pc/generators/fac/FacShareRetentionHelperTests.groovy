package org.pillarone.riskanalytics.domain.pc.generators.fac

import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import umontreal.iro.lecuyer.probdist.DiscreteDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FacShareRetentionHelperTests extends GroovyTestCase {

    static final double EPSILON = 1E-10

    void testUsage() {
        FacShareRetentionHelper helper = new FacShareRetentionHelper()
        helper.add(1, 0.2, 0.01, 0.11)
        helper.add(2, 0.4, 0.02, 0.12)
        helper.add(3, 0.6, 0.03, 0.13)
        helper.add(4, 0.8, 0.04, 0.14)

        RandomDistribution distributionFacQuotaShares = helper.getFacQuotaShareDistribution()

        assertEquals 'fac share distribution type', DiscreteDistribution.class, distributionFacQuotaShares.distribution.class
        assertEquals 'fac share mean', 0.6, distributionFacQuotaShares.distribution.mean, EPSILON

        RandomDistribution distributionFacSurplus = helper.getFacSurplusSharesDistribution()

        assertEquals 'fac share distribution type', DiscreteDistribution.class, distributionFacSurplus.distribution.class
        assertEquals 'fac share mean', 0.03, distributionFacSurplus.distribution.mean, EPSILON


        RandomDistribution distributionFacRetentions = helper.getFacRetentionDistribution()

        assertEquals 'fac retention distribution type', DiscreteDistribution.class, distributionFacRetentions.distribution.class
        assertEquals 'fac retention mean', 0.13, distributionFacRetentions.distribution.mean, EPSILON
    }
}

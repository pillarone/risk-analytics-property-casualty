package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingSegment
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.constants.Exposure

class LossTableGeneratorTests extends GroovyTestCase {

    LossTableGenerator generator
    UnderwritingSegment segment
    UnderwritingSegment segment1
    UnderwritingSegment segment2

    void testUsage() {
        generator = new LossTableGenerator(parmLossTable: [10, 20, 30, 40, 50])

        generator.inClaimCount << new Frequency(value: 3)
        generator.doCalculation()

        assertTrue("number of claims = 3", generator.outClaims.size() == 3)
        assertTrue("3.1", 10 == generator.outClaims[0].ultimate)
        assertTrue("3.2", 20 == generator.outClaims[1].ultimate)
        assertTrue("3.3", 30 == generator.outClaims[2].ultimate)

        generator.reset()
        generator.inClaimCount << new Frequency(value: 7)
        generator.doCalculation()

        assertTrue("number of claims = 7", generator.outClaims.size() == 7)
        assertTrue("7.1", 10 == generator.outClaims[0].ultimate)
        assertTrue("7.2", 20 == generator.outClaims[1].ultimate)
        assertTrue("7.3", 30 == generator.outClaims[2].ultimate)
        assertTrue("7.3", 40 == generator.outClaims[3].ultimate)
        assertTrue("7.3", 50 == generator.outClaims[4].ultimate)
        assertTrue("7.3", 10 == generator.outClaims[5].ultimate)
        assertTrue("7.3", 20 == generator.outClaims[6].ultimate)
    }

    void testValidateWiring() {
        generator = new LossTableGenerator(parmLossTable: [10, 20, 30, 40, 50])
        segment = new UnderwritingSegment(parmWrittenExposure: 100)

        WiringUtils.use(WireCategory) {
            generator.inUnderwritingInfo = segment.outUnderwritingInfo
        }
        generator.validateWiring()
    }

    void testValidateWiring2UWInfo() {
        generator = new LossTableGenerator(parmLossTable: [10, 20, 30, 40, 50])
        segment1 = new UnderwritingSegment(parmWrittenExposure: 100)
        segment2 = new UnderwritingSegment(parmWrittenExposure: 100)

        WiringUtils.use(WireCategory) {
            generator.inUnderwritingInfo = segment1.outUnderwritingInfo
            generator.inUnderwritingInfo = segment2.outUnderwritingInfo
        }
        shouldFail IllegalStateException, { generator.validateWiring() }
    }

    void testValidateParameterizationByPeriod() {
        generator = new LossTableGenerator(parmLossTable: [10, 20, 30, 40, 50], parmBase: Exposure.PREMIUM_WRITTEN)
        shouldFail IllegalStateException, { generator.validateParameterization() }
    }
}
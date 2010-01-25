package org.pillarone.riskanalytics.domain.pc.underwriting

import org.pillarone.riskanalytics.domain.pc.constants.Exposure

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingSegmentTests extends GroovyTestCase {

    void testUsage() {
        double pricePerExposureUnit = 500
        double writtenExposure = 1000

        UnderwritingSegment segment = new UnderwritingSegment(
                parmPricePerExposureUnit: pricePerExposureUnit,
                parmWrittenExposure: writtenExposure,
                parmExposureDefinition: Exposure.ABSOLUTE
        )

        segment.doCalculation()

        assertTrue "premiumWritten", pricePerExposureUnit * writtenExposure == segment.outUnderwritingInfo[0].premiumWritten
        assertTrue "numberOfPolicies (uw info)", Double.NaN == segment.outUnderwritingInfo[0].numberOfPolicies
    }
}
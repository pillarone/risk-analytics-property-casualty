package org.pillarone.riskanalytics.domain.pc.allocators

import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBandsTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com, michael-noe (at) web (dot) de
 */

public class FrequencyAllocatorTests extends GroovyTestCase {

    void testUsage() {
        FrequencyAllocator frequencyAllocator = new FrequencyAllocator()
        frequencyAllocator.inFrequency << new Frequency(value: 5)
        frequencyAllocator.inUnderwritingInfo.addAll RiskBandsTests.underwritingInfos

        frequencyAllocator.doCalculation()

        assertTrue "correct number of frequencies", frequencyAllocator.inFrequency[0].value == frequencyAllocator.outFrequency.value.sum() 
    }
}
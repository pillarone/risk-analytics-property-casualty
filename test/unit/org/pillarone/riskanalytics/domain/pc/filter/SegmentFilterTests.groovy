package org.pillarone.riskanalytics.domain.pc.filter

import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.lob.ConfigurableLob
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentFilterTests extends GroovyTestCase {

    ConfigurableLob lobMotor = new ConfigurableLob(name: 'motor')
    ConfigurableLob lobMotorHull = new ConfigurableLob(name: 'motor hull')
    ConfigurableLob lobAccident = new ConfigurableLob(name: 'accident')
    ClaimDevelopmentLeanPacket claimMotor100 = new ClaimDevelopmentLeanPacket(lineOfBusiness: lobMotor, incurred: 100)
    ClaimDevelopmentLeanPacket claimMotorHull500 = new ClaimDevelopmentLeanPacket(lineOfBusiness: lobMotorHull, incurred: 500)
    ClaimDevelopmentLeanPacket claimAccident80 = new ClaimDevelopmentLeanPacket(lineOfBusiness: lobAccident, incurred: 80)
    UnderwritingInfo underwritingInfoMotor50 = new UnderwritingInfo(lineOfBusiness: lobMotor, premium: 50)
    UnderwritingInfo underwritingInfoMotorHull40 = new UnderwritingInfo(lineOfBusiness: lobMotorHull, premium: 40)
    UnderwritingInfo underwritingInfoAccident30 = new UnderwritingInfo(lineOfBusiness: lobAccident, premium: 30)

    void testUsage() {
        SegmentFilter segmentFilter = new SegmentFilter(parmPortions: new ConstrainedMultiDimensionalParameter(
            [['motor', 'motor hull'],[0.8, 0.5]], Arrays.asList(SegmentFilter.SEGMENT, SegmentFilter.PORTION),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER)))

        segmentFilter.inClaimsGross << claimMotor100 << claimMotorHull500 << claimAccident80
        segmentFilter.inUnderwritingInfoGross << underwritingInfoMotor50 << underwritingInfoMotorHull40 << underwritingInfoAccident30

        new TestProbe(segmentFilter, 'outClaimsGross')
        new TestProbe(segmentFilter, 'outUnderwritingInfoGross')

        segmentFilter.doCalculation()

        assertEquals '#claims, gross', 2, segmentFilter.outClaimsGross.size()
        assertEquals 'claimMoter100', 80, segmentFilter.outClaimsGross[0].incurred
        assertEquals 'claimMotorHull500', 250, segmentFilter.outClaimsGross[1].incurred
        assertEquals '#claims, ceded', 0, segmentFilter.outClaimsCeded.size()
        assertEquals '#claims, net', 0, segmentFilter.outClaimsNet.size()
        assertEquals '#underwriting info, gross', 2, segmentFilter.outUnderwritingInfoGross.size()
        assertEquals 'underwritingInfoMotor50', 40, segmentFilter.outUnderwritingInfoGross[0].premium
        assertEquals 'underwritingInfoMotorHull40', 20, segmentFilter.outUnderwritingInfoGross[1].premium
        assertEquals '#underwriting info, ceded', 0, segmentFilter.outUnderwritingInfoCeded.size()
        assertEquals '#underwriting info, net', 0, segmentFilter.outUnderwritingInfoNet.size()
    }
    
    void testCededChannels() {
        SegmentFilter segmentFilter = new SegmentFilter(parmPortions: new ConstrainedMultiDimensionalParameter(
            [['accident'],[0.2]], Arrays.asList(SegmentFilter.SEGMENT, SegmentFilter.PORTION),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER)))

        segmentFilter.inClaimsCeded << claimMotor100 << claimMotorHull500 << claimAccident80
        segmentFilter.inUnderwritingInfoCeded << underwritingInfoMotor50 << underwritingInfoMotorHull40 << underwritingInfoAccident30

        new TestProbe(segmentFilter, 'outClaimsCeded')
        new TestProbe(segmentFilter, 'outUnderwritingInfoCeded')

        segmentFilter.doCalculation()

        assertEquals '#claims, gross', 0, segmentFilter.outClaimsGross.size()
        assertEquals '#claims, ceded', 1, segmentFilter.outClaimsCeded.size()
        assertEquals 'claimAccident80', 16, segmentFilter.outClaimsCeded[0].incurred
        assertEquals '#claims, net', 0, segmentFilter.outClaimsNet.size()
        assertEquals '#underwriting info, gross', 0, segmentFilter.outUnderwritingInfoGross.size()
        assertEquals '#underwriting info, ceded', 1, segmentFilter.outUnderwritingInfoCeded.size()
        assertEquals 'underwritingInfoAccident30', 6, segmentFilter.outUnderwritingInfoCeded[0].premium
        assertEquals '#underwriting info, net', 0, segmentFilter.outUnderwritingInfoNet.size()
    }

    void testNetChannels() {
        SegmentFilter segmentFilter = new SegmentFilter(parmPortions: new ConstrainedMultiDimensionalParameter(
            [['accident'],[0.2]], Arrays.asList(SegmentFilter.SEGMENT, SegmentFilter.PORTION),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER)))

        segmentFilter.inClaimsNet << claimMotor100 << claimMotorHull500 << claimAccident80
        segmentFilter.inUnderwritingInfoNet << underwritingInfoMotor50 << underwritingInfoMotorHull40 << underwritingInfoAccident30

        new TestProbe(segmentFilter, 'outClaimsNet')
        new TestProbe(segmentFilter, 'outUnderwritingInfoNet')

        segmentFilter.doCalculation()

        assertEquals '#claims, gross', 0, segmentFilter.outClaimsGross.size()
        assertEquals '#claims, ceded', 0, segmentFilter.outClaimsCeded.size()
        assertEquals '#claims, net', 1, segmentFilter.outClaimsNet.size()
        assertEquals 'claimAccident80', 16, segmentFilter.outClaimsNet[0].incurred
        assertEquals '#underwriting info, gross', 0, segmentFilter.outUnderwritingInfoGross.size()
        assertEquals '#underwriting info, ceded', 0, segmentFilter.outUnderwritingInfoCeded.size()
        assertEquals '#underwriting info, net', 1, segmentFilter.outUnderwritingInfoNet.size()
        assertEquals 'underwritingInfoAccident30', 6, segmentFilter.outUnderwritingInfoNet[0].premium
    }

    void testNoPortions() {
        SegmentFilter segmentFilter = new SegmentFilter(parmPortions: new ConstrainedMultiDimensionalParameter(
            [[],[]], Arrays.asList(SegmentFilter.SEGMENT, SegmentFilter.PORTION),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER)))

        segmentFilter.inClaimsNet << claimMotor100 << claimMotorHull500 << claimAccident80
        segmentFilter.inUnderwritingInfoNet << underwritingInfoMotor50 << underwritingInfoMotorHull40 << underwritingInfoAccident30

        new TestProbe(segmentFilter, 'outClaimsNet')
        new TestProbe(segmentFilter, 'outUnderwritingInfoNet')

        segmentFilter.doCalculation()

        assertEquals '#claims, gross', 0, segmentFilter.outClaimsGross.size()
        assertEquals '#claims, ceded', 0, segmentFilter.outClaimsCeded.size()
        assertEquals '#claims, net', 0, segmentFilter.outClaimsNet.size()
        assertEquals '#underwriting info, gross', 0, segmentFilter.outUnderwritingInfoGross.size()
        assertEquals '#underwriting info, ceded', 0, segmentFilter.outUnderwritingInfoCeded.size()
        assertEquals '#underwriting info, net', 0, segmentFilter.outUnderwritingInfoNet.size()
    }
}

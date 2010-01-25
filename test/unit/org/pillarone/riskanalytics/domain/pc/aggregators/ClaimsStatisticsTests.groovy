package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket

class ClaimsStatisticsTests extends GroovyTestCase {

    void testSimpleTally() {
        PacketList<SingleValuePacket> values = new PacketList(SingleValuePacket)

        values << new SingleValuePacket(value: 1)
        values << new SingleValuePacket(value: 2)
        values << new SingleValuePacket(value: 3)

        ClaimsStatistics claimsStatistics = new ClaimsStatistics()

        claimsStatistics.inValues = values
        claimsStatistics.doCalculation()

        assertEquals 1, claimsStatistics.outStatistics.size()
        assertEquals 3, claimsStatistics.outStatistics[0].tally.max()
        assertEquals 2, claimsStatistics.outStatistics[0].tally.average()
        assertEquals 6, claimsStatistics.outStatistics[0].tally.sum()
    }

    void testTallyWithTypes() {
        PacketList<SingleValuePacket> values = new PacketList(SingleValuePacket)

        values << new SingleValuePacket(value: 1)
        values << new SingleValuePacket(value: 2)
        values << new SingleValuePacket(value: 3)

        ClaimsStatistics claimsStatistics = new ClaimsStatistics()

        claimsStatistics.inValues = values
        claimsStatistics.doCalculation()

        assertEquals 1, claimsStatistics.outStatistics.size()

        def fooStat = claimsStatistics.outStatistics.find {
            it.type == 'SingleValuePacket'
        }

        assertEquals 3, fooStat.tally.max()
        assertEquals 2, fooStat.tally.average()
        assertEquals 6, fooStat.tally.sum()
    }
}
package org.pillarone.riskanalytics.domain.pc.aggregators

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import umontreal.iro.lecuyer.stat.TallyStore
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.domain.utils.Statistic

class ClaimsStatistics extends Component {

    PacketList<SingleValuePacket> inValues = new PacketList(SingleValuePacket)
    PacketList<Statistic> outStatistics = new PacketList(Statistic)


    public void doCalculation() {

        Map<String, Statistic> statistics = new HashMap()

        inValues.each {current ->
            if (!statistics.containsKey(current.class.getSimpleName())) {
                TallyStore tally = new TallyStore(current.class.getSimpleName())
                Statistic statistic = new Statistic(tally: tally, type: current.class.getSimpleName())
                statistics.put(current.class.getSimpleName(), statistic)
                tally.add(current.value)
                outStatistics << statistic
            }
            else {
                Statistic statistic = statistics.get(current.class.getSimpleName())
                statistic.tally.add(current.value)
            }
        }

    }
}

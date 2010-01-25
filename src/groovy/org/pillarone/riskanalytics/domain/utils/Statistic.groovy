package org.pillarone.riskanalytics.domain.utils

import org.pillarone.riskanalytics.core.packets.Packet
import umontreal.iro.lecuyer.stat.TallyStore
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

class Statistic extends Packet {
    TallyStore tally
    String type

    def calculateQuantile(alpha) {
        def values = new double[tally.numberObs()]
        System.arraycopy(tally.getArray(), 0, values, 0, tally.numberObs())
        RandomVariateDistribution distribution = RandomVariateDistributionFactory.getDistribution(DistributionType.PIECEWISELINEAREMPIRICAL,
            ["observations": new TableMultiDimensionalParameter([values.toList()], ["observations"])])
        distribution.inverseDistributionFunction(alpha)
    }

    def calculateCenteredQuantile(alpha) {
        calculateQuantile(alpha) - tally.average()
    }
}

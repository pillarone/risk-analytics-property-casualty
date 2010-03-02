package org.pillarone.riskanalytics.domain.pc.generators.severities

import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.utils.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList

/**
 *  The event severity generator produces event severities with number of
 *  individual severities given by the external frequency and
 *  Uniform-distributed individual severities.<br/>
 *  The provided severities at <tt>outSeverities</tt> are in
 *  the range of [0..1].
 *  No parameters are needed to specify a uniform distribution.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class EventSeverityGenerator extends GeneratorCachingComponent {

    RandomDistribution parmDistribution = RandomDistributionFactory.getDistribution(DistributionType.UNIFORM, ["a": 0, "b": 1]);
    DistributionModified parmModification = DistributionModifierFactory.getModifier(DistributionModifier.NONE, new HashMap());

    /** Input channel for the number of severities to be generated     */
    PacketList<Frequency> inSeverityCount = new PacketList(Frequency)
    /** Output channel for severities.     */
    PacketList<EventSeverity> outSeverities = new PacketList(EventSeverity)

    private IRandomNumberGenerator generator;

    public void validateParameterization() {
        if (parmDistribution == null) {
            throw new IllegalStateException("A distribution must be set");
        }
        super.validateParameterization();
    }

    public void doCalculation() {
        generator = getCachedGenerator(parmDistribution, parmModification);
        int numberOfEvents = inSeverityCount.value.sum()
        List<Double> dates = UniformDoubleList.getDoubles(numberOfEvents, true)
        dates.sort()

        int counter = -1
        for (int i = 0; i < inSeverityCount.size(); i++) {
            counter++
            for (int j = 0; j < inSeverityCount[i].value; j++) {
                outSeverities << new EventSeverity(
                    value: (Double) generator.nextValue(),
                    event: new Event(date: dates[counter])
                )
            }
        }
    }
}

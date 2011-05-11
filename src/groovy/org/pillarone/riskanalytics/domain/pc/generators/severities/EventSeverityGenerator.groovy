package org.pillarone.riskanalytics.domain.pc.generators.severities

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList
import org.pillarone.riskanalytics.domain.utils.*
import org.pillarone.riskanalytics.core.components.ComponentCategory

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
@ComponentCategory(categories = ['GENERATOR','EVENT'])
class EventSeverityGenerator extends GeneratorCachingComponent {

    RandomDistribution parmDistribution = DistributionType.getStrategy(DistributionType.UNIFORM, ["a": 0, "b": 1]);
    DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());

    /** Input channel for the number of severities to be generated     */
    PacketList<Frequency> inSeverityCount = new PacketList(Frequency)
    /** Output channel for severities.     */
    PacketList<EventSeverity> outSeverities = new PacketList(EventSeverity)

    private IRandomNumberGenerator generator;

    public void validateParameterization() {
        if (parmDistribution == null) {
            throw new IllegalStateException("EventSeverityGenerator.missingDistribution");
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
                    event: new Event(fractionOfPeriod: dates[counter])
                )
            }
        }
    }
}

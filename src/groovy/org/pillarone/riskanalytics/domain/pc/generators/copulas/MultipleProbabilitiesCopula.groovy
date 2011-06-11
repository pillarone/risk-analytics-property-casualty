package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.pc.generators.frequency.Frequency
import org.pillarone.riskanalytics.domain.pc.generators.frequency.FrequencyPacketFactory
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity
import org.pillarone.riskanalytics.domain.utils.randomnumbers.UniformDoubleList
import org.pillarone.riskanalytics.domain.utils.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultipleProbabilitiesCopula extends GeneratorCachingComponent {

    private DistributionModified modifier = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());
    private RandomDistribution parmFrequencyDistribution = DistributionType.getStrategy(
            FrequencyDistributionType.CONSTANT, ['constant': 0d])

    private ICopulaStrategy parmCopulaStrategy = CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.INDEPENDENT,
            ["targets": new ComboBoxTableMultiDimensionalParameter([''], ['perils'], IPerilMarker)])

    private IRandomNumberGenerator generator;

    private PacketList<EventDependenceStream> outEventSeverities = new PacketList(EventDependenceStream.class);
    private PacketList<Frequency> outFrequency = new PacketList(Frequency.class);

    public void validateParameterization() {
        if (parmFrequencyDistribution == null) {
            throw new IllegalStateException("MultipleProbabilitiesCopula.missingDistribution");
        }
        super.validateParameterization();
    }

    public void doCalculation() {

        generator = getCachedGenerator(parmFrequencyDistribution, modifier);
        int frequency = generator.nextValue().intValue();
        List<Double> dates = UniformDoubleList.getDoubles(frequency, true);
        for (int i = 0; i < frequency; i++) {
            outEventSeverities.add(new EventDependenceStream(
                    severities: buildEventSeverity(new Event(fractionOfPeriod: dates.get(i))),
                    marginals: parmCopulaStrategy.getTargetNames()))
        }
        if (isSenderWired(outFrequency)) {
            Frequency frequencyPacket = FrequencyPacketFactory.createPacket();
            frequencyPacket.value = frequency;
            outFrequency.add(frequencyPacket);
        }
    }

    private List<EventSeverity> buildEventSeverity(Event event) {
        List<EventSeverity> eventSeverities = new ArrayList<EventSeverity>();
        List<Double> probabilities = parmCopulaStrategy.getRandomVector();
        for (int i = 0; i < probabilities.size(); i++) {
            eventSeverities.add(new EventSeverity(event: event, value: probabilities.get(i)))
        }
        return eventSeverities;
    }

    public DistributionModified getModifier() {
        return modifier;
    }

    public void setModifier(DistributionModified modifier) {
        this.modifier = modifier;
    }

    public RandomDistribution getParmFrequencyDistribution() {
        return parmFrequencyDistribution;
    }

    public void setParmFrequencyDistribution(RandomDistribution parmDistribution) {
        this.parmFrequencyDistribution = parmDistribution;
    }

    public PacketList<EventDependenceStream> getOutEventSeverities() {
        return outEventSeverities;
    }

    public void setOutEventSeverities(PacketList<EventDependenceStream> outEventSeverities) {
        this.outEventSeverities = outEventSeverities;
    }

    public PacketList<Frequency> getOutFrequency() {
        return outFrequency;
    }

    public void setOutFrequency(PacketList<Frequency> outFrequency) {
        this.outFrequency = outFrequency;
    }

    public ICopulaStrategy getParmCopulaStrategy() {
        return parmCopulaStrategy;
    }

    public void setParmCopulaStrategy(ICopulaStrategy parmCopulaStrategy) {
        this.parmCopulaStrategy = parmCopulaStrategy;
    }
}
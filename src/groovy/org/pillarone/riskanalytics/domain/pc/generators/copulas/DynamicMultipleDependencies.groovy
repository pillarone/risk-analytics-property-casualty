package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.utils.DistributionModifierFactory
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.RandomDistributionFactory
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicMultipleDependencies extends DynamicComposedComponent {

    private PacketList<EventDependenceStream> outEventSeverities = new PacketList(EventDependenceStream.class);

    public MultipleProbabilitiesCopula createDefaultSubComponent() {
        MultipleProbabilitiesCopula newComponent = new MultipleProbabilitiesCopula(
                modifier: DistributionModifierFactory.getModifier(DistributionModifier.NONE, [:]),
                parmFrequencyDistribution: RandomDistributionFactory.getDistribution(
                        FrequencyDistributionType.CONSTANT, ["constant": 1d]),
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.INDEPENDENT,
                        ["targets": new ComboBoxTableMultiDimensionalParameter([''], ['perils'], PerilMarker)]))
        return newComponent
    }

    protected void doCalculation() {
        for (MultipleProbabilitiesCopula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outEventSeverities'
    }

    public void setOutEventSeverities(PacketList<EventDependenceStream> outEventSeverities) {
        this.outEventSeverities = outEventSeverities;
    }

    public PacketList<EventDependenceStream> getOutEventSeverities() {
        return outEventSeverities;
    }
}
package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.DistributionType
import org.pillarone.riskanalytics.domain.utils.FrequencyDistributionType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DynamicMultipleDependencies extends DynamicComposedComponent {

    private PacketList<EventDependenceStream> outEventSeverities = new PacketList(EventDependenceStream.class);

    public MultipleProbabilitiesCopula createDefaultSubComponent() {
        MultipleProbabilitiesCopula newComponent = new MultipleProbabilitiesCopula(
                modifier: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                parmFrequencyDistribution: DistributionType.getStrategy(
                        FrequencyDistributionType.CONSTANT, ["constant": 1d]),
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.INDEPENDENT,
                        ["targets": new ComboBoxTableMultiDimensionalParameter([''], ['perils'], IPerilMarker)]))
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
package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class DynamicDependencies extends DynamicComposedComponent {

    private PacketList<DependenceStream> outProbabilities = new PacketList(DependenceStream.class);

    public Copula createDefaultSubComponent() {
        return new PerilCopula(
                parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(PerilCopulaType.INDEPENDENT,
                        ["targets": new ComboBoxTableMultiDimensionalParameter([''], ['perils'], IPerilMarker)]))
    }

    protected void doCalculation() {
        for (Copula component: componentList) {
            component.start()
        }
    }

    public void wire() {
        replicateOutChannels this, 'outProbabilities'
    }

    public void setOutProbabilities(PacketList<DependenceStream> outProbabilities) {
        this.outProbabilities = outProbabilities;
    }

    public PacketList<DependenceStream> getOutProbabilities() {
        return outProbabilities;
    }
}
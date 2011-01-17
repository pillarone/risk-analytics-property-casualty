package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.IStructureInformation;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoFilter extends Component {

    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private SimulationScope simulationScope;
    private static Log LOG = LogFactory.getLog(ClaimsFilter.class);


    public void doCalculation() {
        String componentLine = simulationScope.getStructureInformation().getLine(this).toLowerCase();
        for (UnderwritingInfo underwritingInfo: inUnderwritingInfo) {
            String packetLine;
            IStructureInformation structureInformation = simulationScope.getStructureInformation();
            if (underwritingInfo.getOriginalUnderwritingInfo() == null) {
                packetLine = structureInformation.getLine(underwritingInfo).toLowerCase();
            }
            else {
                packetLine = structureInformation.getLine(underwritingInfo.getOriginalUnderwritingInfo()).toLowerCase();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("UnderwritingInfoFilter matching: ${packetLine.equals(componentLine)}, packetLine: ${packetLine}, componentLine: ${componentLine}, path: ${structureInformation.getPath(underwritingInfo)}");
            }
            if (packetLine.equals(componentLine)) {
                outUnderwritingInfo.add(underwritingInfo);
            }
        }
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfo() {
        return outUnderwritingInfo;
    }

    public void setOutUnderwritingInfo(PacketList<UnderwritingInfo> outUnderwritingInfo) {
        this.outUnderwritingInfo = outUnderwritingInfo;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationContext) {
        this.simulationScope = simulationContext;
    }
}
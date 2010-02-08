package org.pillarone.riskanalytics.domain.pc.aggregators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.IStructureInformation;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsFilter extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);

    private SimulationScope simulationScope;
    private static Log LOG = LogFactory.getLog(ClaimsFilter.class);

    public void doCalculation() {
        String componentLine = simulationScope.getStructureInformation().getLine(this).toLowerCase();
        for (Claim claim: inClaims) {
            String packetLine;
            IStructureInformation structureInformation = simulationScope.getStructureInformation();
            if (claim.getOriginalClaim() == null) {
                packetLine = structureInformation.getLine(claim).toLowerCase();
            }
            else {
                packetLine = structureInformation.getLine(claim.getOriginalClaim()).toLowerCase();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("ClaimsFilter matching: ${packetLine.equals(componentLine)}, packetLine: ${packetLine}, componentLine: ${componentLine}, path: ${structureInformation.getPath(claim)}");
            }
            if (packetLine.equals(componentLine)) {
                outClaims.add(claim);
            }
        }
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }
}
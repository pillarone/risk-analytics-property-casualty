package org.pillarone.riskanalytics.domain.pc.reinsurance.commissions;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.applicable.*;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  This component calculates the commission on a specified set of contracts, defined in parameter
 *  parmApplicableContracts, according to a rule specified in parameter parmCommissionStrategy.
 *
 *  Implementation Note: the incoming packets are first filtered to determine which are applicable
 *  to the commission calculation. The outUnderwritingInfo packet stream is split into those packets
 *  to which the commission applies, which get modified, and those which don't apply and are not modified.
 *
 *  If you want all outPackets, just wire outUnderwritingInfoModified & outUnderwritingInfoUnmodified
 *  to the same inChannel, and be aware that the order may not match that of inUnderwritingInfo.
 *
 *  @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class Commission extends Component {

    private SimulationScope simulationScope;

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<UnderwritingInfo> outUnderwritingInfoModified = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoUnmodified = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getStrategy(
            CommissionStrategyType.FIXEDCOMMISSION, ArrayUtils.toMap(new Object[][]{{"commission", 0d}}));

    private IApplicableStrategy parmApplicableStrategy = ApplicableStrategyType.getStrategy(
            ApplicableStrategyType.NONE, Collections.emptyMap());

    protected void doCalculation() {
        if (parmApplicableStrategy instanceof NoneApplicableStrategy) {
            outUnderwritingInfoUnmodified.addAll(inUnderwritingInfo);
        } else {
            boolean isFirstPeriod = simulationScope.getIterationScope().getPeriodScope().getCurrentPeriod() == 0;

            if (parmApplicableStrategy instanceof ContractApplicableStrategy) {
                List<IReinsuranceContractMarker> applicableContracts = ((IContractApplicableStrategy) parmApplicableStrategy)
                                             .getApplicableContracts().getValuesAsObjects(getSimulationScope().getModel());
                PacketList<Claim> filteredClaims = new PacketList<Claim>(Claim.class);
                filteredClaims.addAll(ClaimFilterUtilities.filterClaimsByContract(inClaims, applicableContracts));

                List<UnderwritingInfo> applicableUnderwritingInfo = new ArrayList<UnderwritingInfo>();
                List<UnderwritingInfo> irrelevantUnderwritingInfo = new ArrayList<UnderwritingInfo>();

                UnderwritingInfoUtilities.segregateUnderwritingInfoByContract(inUnderwritingInfo, applicableContracts,
                                                                      applicableUnderwritingInfo, irrelevantUnderwritingInfo);
                parmCommissionStrategy.calculateCommission(filteredClaims, applicableUnderwritingInfo, isFirstPeriod, true);
                outUnderwritingInfoModified.addAll(applicableUnderwritingInfo);
                outUnderwritingInfoUnmodified.addAll(irrelevantUnderwritingInfo);
            }
            else if (parmApplicableStrategy instanceof AllApplicableStrategy) {
                parmCommissionStrategy.calculateCommission(inClaims, inUnderwritingInfo, isFirstPeriod, true);
                outUnderwritingInfoModified.addAll(inUnderwritingInfo);
            }
            else {
                throw new NotImplementedException(parmApplicableStrategy.toString() + " type is not yet implemented.");
            }
        }
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfo> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public ICommissionStrategy getParmCommissionStrategy() {
        return parmCommissionStrategy;
    }

    public void setParmCommissionStrategy(ICommissionStrategy parmCommissionStrategy) {
        this.parmCommissionStrategy = parmCommissionStrategy;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public IApplicableStrategy getParmApplicableStrategy() {
        return parmApplicableStrategy;
    }

    public void setParmApplicableStrategy(IApplicableStrategy parmApplicableStrategy) {
        this.parmApplicableStrategy = parmApplicableStrategy;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoModified() {
        return outUnderwritingInfoModified;
    }

    public void setOutUnderwritingInfoModified(PacketList<UnderwritingInfo> outUnderwritingInfoModified) {
        this.outUnderwritingInfoModified = outUnderwritingInfoModified;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoUnmodified() {
        return outUnderwritingInfoUnmodified;
    }

    public void setOutUnderwritingInfoUnmodified(PacketList<UnderwritingInfo> outUnderwritingInfoUnmodified) {
        this.outUnderwritingInfoUnmodified = outUnderwritingInfoUnmodified;
    }
}
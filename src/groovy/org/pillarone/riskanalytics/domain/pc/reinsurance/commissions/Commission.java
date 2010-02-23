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

import java.util.Collections;
import java.util.List;

/**
 *  This component calculates commission on a specified set of contracts, which are defined in the
 *  parameter parmApplicableContracts.
 *
 *  Implementation Note: the incoming packets are first filtered to determine which are applicable
 *  to the commission calculation. The outUnderwritingInfo packet stream contains the same packets,
 *  but potentially in a different order.
 *
 *  Cave: the outUnderwritingInfo packet sequence will differ from the corresponding inUnderwritingInfo
 *  sequence whenever the incoming sequence is actually filtered, because packets that are used (which
 *  can affect the commission calculation) are placed on the out- packet list before the packets that
 *  got filtered out (which cannot affect the commission calculation).
 *
 *  @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class Commission extends Component {

    private SimulationScope simulationScope;

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getStrategy(
            CommissionStrategyType.FIXEDCOMMISSION, ArrayUtils.toMap(new Object[][]{{"commission", 0d}}));

    private IApplicableStrategy parmApplicableStrategy = ApplicableStrategyType.getStrategy(
            ApplicableStrategyType.NONE, Collections.emptyMap());

    protected void doCalculation() {
        if (parmApplicableStrategy instanceof NoneApplicableStrategy) {
            outUnderwritingInfo.addAll(inUnderwritingInfo);
        } else {
            boolean isFirstPeriod = simulationScope.getIterationScope().getPeriodScope().getCurrentPeriod() == 0;

            if (parmApplicableStrategy instanceof ContractApplicableStrategy) {
                List<IReinsuranceContractMarker> applicableContracts = ((IContractApplicableStrategy) parmApplicableStrategy)
                                             .getApplicableContracts().getValuesAsObjects(getSimulationScope().getModel());
                PacketList<Claim> filteredClaims = new PacketList<Claim>(Claim.class);
                filteredClaims.addAll(ClaimFilterUtilities.filterClaimsByContract(inClaims, applicableContracts));

                PacketList<UnderwritingInfo> bypassedUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
                PacketList<UnderwritingInfo> filteredUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

                UnderwritingInfoUtilities.segregateUnderwritingInfoByContract(inUnderwritingInfo, applicableContracts, filteredUnderwritingInfo, bypassedUnderwritingInfo);
                parmCommissionStrategy.calculateCommission(filteredClaims, filteredUnderwritingInfo, isFirstPeriod);
                outUnderwritingInfo.addAll(filteredUnderwritingInfo);
                outUnderwritingInfo.addAll(bypassedUnderwritingInfo);
            }
            else if (parmApplicableStrategy instanceof AllApplicableStrategy) {
                parmCommissionStrategy.calculateCommission(inClaims, inUnderwritingInfo, isFirstPeriod);
                outUnderwritingInfo.addAll(inUnderwritingInfo);
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

    public PacketList<UnderwritingInfo> getOutUnderwritingInfo() {
        return outUnderwritingInfo;
    }

    public void setOutUnderwritingInfo(PacketList<UnderwritingInfo> outUnderwritingInfo) {
        this.outUnderwritingInfo = outUnderwritingInfo;
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
}
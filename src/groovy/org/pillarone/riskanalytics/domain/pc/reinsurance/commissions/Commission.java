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
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfoUtilities;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This component calculates the commission on a specified set of contracts, defined in parameter
 * parmApplicableContracts, according to a rule specified in parameter parmCommissionStrategy.
 * <p/>
 * Implementation Note: the incoming packets are first filtered to determine which are applicable
 * to the commission calculation. The outUnderwritingInfo packet stream is split into those packets
 * to which the commission applies, which get modified, and those which don't apply and are not modified.
 * <p/>
 * If you want all outPackets, just wire outUnderwritingInfoModified & outUnderwritingInfoUnmodified
 * to the same inChannel, and be aware that the order may not match that of inUnderwritingInfo.
 *
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class Commission extends Component {

    private SimulationScope simulationScope;

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<CededUnderwritingInfo> inUnderwritingInfo = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);

    private PacketList<CededUnderwritingInfo> outUnderwritingInfoModified = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);
    private PacketList<CededUnderwritingInfo> outUnderwritingInfoUnmodified = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);

    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getStrategy(
            CommissionStrategyType.FIXEDCOMMISSION, ArrayUtils.toMap(new Object[][]{{"commission", 0d}}));

    private IApplicableStrategy parmApplicableStrategy = ApplicableStrategyType.getStrategy(
            ApplicableStrategyType.NONE, Collections.emptyMap());

    protected void doCalculation() {
        if (parmApplicableStrategy instanceof NoneApplicableStrategy) {
            outUnderwritingInfoUnmodified.addAll(inUnderwritingInfo);
        }
        else {
            boolean isFirstPeriod = simulationScope.getIterationScope().getPeriodScope().getCurrentPeriod() == 0;

            if (parmApplicableStrategy instanceof ContractApplicableStrategy) {
                List<IReinsuranceContractMarker> applicableContracts = ((IContractApplicableStrategy) parmApplicableStrategy)
                        .getApplicableContracts().getValuesAsObjects();
                PacketList<Claim> filteredClaims = new PacketList<Claim>(Claim.class);
                filteredClaims.addAll(ClaimFilterUtilities.filterClaimsByContract(inClaims, applicableContracts));

                List<CededUnderwritingInfo> applicableUnderwritingInfo = new ArrayList<CededUnderwritingInfo>();
                List<CededUnderwritingInfo> irrelevantUnderwritingInfo = new ArrayList<CededUnderwritingInfo>();

                CededUnderwritingInfoUtilities.segregateUnderwritingInfoByContract(inUnderwritingInfo, applicableContracts,
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
                throw new NotImplementedException("['Commission.notImplemented','" + parmApplicableStrategy.toString() + "']");
            }
        }
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<CededUnderwritingInfo> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<CededUnderwritingInfo> inUnderwritingInfo) {
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

    public PacketList<CededUnderwritingInfo> getOutUnderwritingInfoModified() {
        return outUnderwritingInfoModified;
    }

    public void setOutUnderwritingInfoModified(PacketList<CededUnderwritingInfo> outUnderwritingInfoModified) {
        this.outUnderwritingInfoModified = outUnderwritingInfoModified;
    }

    public PacketList<CededUnderwritingInfo> getOutUnderwritingInfoUnmodified() {
        return outUnderwritingInfoUnmodified;
    }

    public void setOutUnderwritingInfoUnmodified(PacketList<CededUnderwritingInfo> outUnderwritingInfoUnmodified) {
        this.outUnderwritingInfoUnmodified = outUnderwritingInfoUnmodified;
    }
}
package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.reinsurance.ReinsuranceResultWithCommissionPacket;
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.CommissionStrategyType;
import org.pillarone.riskanalytics.domain.pc.reinsurance.commissions.ICommissionStrategy;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContract extends Component implements IReinsuranceContractMarker {

    /** Defines the kind of contract and parameterization      */
    protected IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractStrategyFactory.getTrivial();

    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getNoCommission();

    /**
     *  Defines the claim and underwriting info the contract will receive.
     *  Namely, the net after contracts with lower inuring priority.
     *
     *  Cave: Setting the inuring priority is not trivial. Make sure you have a
     *  correct understanding before 'playing around' with it.
     */
    protected int parmInuringPriority = 0;

    protected PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    protected PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    protected PacketList<SingleValuePacket> inInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    protected PacketList<Claim> outUncoveredClaims = new PacketList<Claim>(Claim.class);
    protected PacketList<Claim> outCoveredClaims = new PacketList<Claim>(Claim.class);

    protected PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    protected PacketList<UnderwritingInfo> outCoverUnderwritingInfo = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    protected PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials = new PacketList<ReinsuranceResultWithCommissionPacket>(ReinsuranceResultWithCommissionPacket.class);

    public void doCalculation() {
        if (parmContractStrategy == null) throw new IllegalStateException("A contract strategy must be set");

        parmContractStrategy.initBookKeepingFigures(inClaims, inUnderwritingInfo);

        Collections.sort(inClaims, SortClaimsByFractionOfPeriod.getInstance());
        if (isSenderWired(outUncoveredClaims)) {
            calculateClaims(inClaims, outCoveredClaims, outUncoveredClaims, this);
        } else {
            calculateCededClaims(inClaims, outCoveredClaims, this);
        }

        if (isSenderWired(outNetAfterCoverUnderwritingInfo)) {
            calculateUnderwritingInfos(inUnderwritingInfo, outCoverUnderwritingInfo, outNetAfterCoverUnderwritingInfo);
        } else if (isSenderWired(outCoverUnderwritingInfo)) {
            calculateCededUnderwritingInfos(inUnderwritingInfo, outCoverUnderwritingInfo);
        }

        parmCommissionStrategy.calculateCommission(outCoveredClaims, outCoverUnderwritingInfo , false);
        if (isSenderWired(getOutContractFinancials())) {
            ReinsuranceResultWithCommissionPacket result = new ReinsuranceResultWithCommissionPacket();
            UnderwritingInfo underwritingInfo = UnderwritingInfoUtilities.aggregate(outCoverUnderwritingInfo);
            if (underwritingInfo != null) {
                result.setCededPremium(-underwritingInfo.getPremiumWritten());
                result.setCededCommission(underwritingInfo.getCommission());
            }
            result.setCededClaim(ClaimUtilities.aggregateClaims(outCoveredClaims, this).getUltimate());
            outContractFinancials.add(result);
        }
        parmContractStrategy.resetMemberInstances();
    }

    public void calculateClaims(List<Claim> grossClaims, List<Claim> cededClaims, List<Claim> netClaims, Component origin) {
        for (Claim claim : grossClaims) {
            Claim cededClaim = getCoveredClaim(claim, origin);
            cededClaims.add(cededClaim);

            Claim claimNet = claim.getNetClaim(cededClaim);
            setClaimReferences(claimNet, claim, origin);
            netClaims.add(claimNet);
        }
    }

    public void calculateCededClaims(List<Claim> grossClaims, List<Claim> cededClaims, Component origin) {
        for (Claim claim : grossClaims) {
            cededClaims.add(getCoveredClaim(claim, origin));
        }
    }

    protected Claim getCoveredClaim(Claim claim, Component origin) {
        double cededFraction = claim.getUltimate() == 0 ? 1d : parmContractStrategy.calculateCoveredLoss(claim) / claim.getUltimate();
        Claim claimCeded = claim.copy();
        claimCeded.scale(cededFraction);
        setClaimReferences(claimCeded, claim, origin);
        return claimCeded;
    }

    private void setClaimReferences(Claim claim, Claim grossClaim, Component origin) {
        claim.origin = origin;
        claim.setReinsuranceContract(this);
        if (grossClaim.getOriginalClaim() != null) {
            claim.setOriginalClaim(grossClaim.getOriginalClaim());
        }
        else {
            claim.setOriginalClaim(grossClaim);
        }
    }

    protected void setOriginalUnderwritingInfo(UnderwritingInfo underwritingInfo, UnderwritingInfo derivedUnderwritingInfo) {
        if (underwritingInfo != null && underwritingInfo.originalUnderwritingInfo != null) {
            derivedUnderwritingInfo.originalUnderwritingInfo = underwritingInfo.originalUnderwritingInfo;
        }
        else {
            derivedUnderwritingInfo.originalUnderwritingInfo = underwritingInfo;
        }
    }

    protected void calculateUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                              List<UnderwritingInfo> cededUnderwritingInfos,
                                              List<UnderwritingInfo> netUnderwritingInfos) {
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            UnderwritingInfo cededUnderwritingInfo = parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, getTotalInitialReserves());
            setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
            UnderwritingInfo netUnderwritingInfo = UnderwritingInfoUtilities.calculateNet(underwritingInfo, cededUnderwritingInfo);
            setOriginalUnderwritingInfo(underwritingInfo, netUnderwritingInfo);
            netUnderwritingInfos.add(netUnderwritingInfo);
        }
    }

    protected void calculateCededUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                                   List<UnderwritingInfo> cededUnderwritingInfos) {
        for (UnderwritingInfo underwritingInfo : grossUnderwritingInfos) {
            UnderwritingInfo cededUnderwritingInfo = parmContractStrategy.calculateCoverUnderwritingInfo(underwritingInfo, getTotalInitialReserves());
            setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
            cededUnderwritingInfos.add(cededUnderwritingInfo);
        }
    }

    private double getTotalInitialReserves() {
        double totalInitialReserves = 0d;
        for(SingleValuePacket initialReserve : inInitialReserves ) {
            totalInitialReserves += initialReserve.getValue();
        }
        return totalInitialReserves;
    }
    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }

    public int getParmInuringPriority() {
        return parmInuringPriority;
    }

    public void setParmInuringPriority(int parmInuringPriority) {
        this.parmInuringPriority = parmInuringPriority;
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

    public PacketList<Claim> getOutUncoveredClaims() {
        return outUncoveredClaims;
    }

    public void setOutUncoveredClaims(PacketList<Claim> outUncoveredClaims) {
        this.outUncoveredClaims = outUncoveredClaims;
    }

    public PacketList<Claim> getOutCoveredClaims() {
        return outCoveredClaims;
    }

    public void setOutCoveredClaims(PacketList<Claim> outCoveredClaims) {
        this.outCoveredClaims = outCoveredClaims;
    }

    public PacketList<UnderwritingInfo> getOutNetAfterCoverUnderwritingInfo() {
        return outNetAfterCoverUnderwritingInfo;
    }

    public void setOutNetAfterCoverUnderwritingInfo(PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo) {
        this.outNetAfterCoverUnderwritingInfo = outNetAfterCoverUnderwritingInfo;
    }

    public PacketList<UnderwritingInfo> getOutCoverUnderwritingInfo() {
        return outCoverUnderwritingInfo;
    }

    public void setOutCoverUnderwritingInfo(PacketList<UnderwritingInfo> outCoverUnderwritingInfo) {
        this.outCoverUnderwritingInfo = outCoverUnderwritingInfo;
    }

    public ICommissionStrategy getParmCommissionStrategy() {
        return parmCommissionStrategy;
    }

    public void setParmCommissionStrategy(ICommissionStrategy parmCommissionStrategy) {
        this.parmCommissionStrategy = parmCommissionStrategy;
    }

    public PacketList<ReinsuranceResultWithCommissionPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(PacketList<ReinsuranceResultWithCommissionPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }
}

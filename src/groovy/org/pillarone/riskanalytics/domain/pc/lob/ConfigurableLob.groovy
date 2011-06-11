package org.pillarone.riskanalytics.domain.pc.lob

import org.pillarone.riskanalytics.domain.pc.aggregators.UnderwritingInfoAggregator
import org.pillarone.riskanalytics.domain.pc.claims.ClaimsFilterByOriginalOrigin
import org.pillarone.riskanalytics.domain.pc.claims.MarketToLineOfBusinessClaims
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterByOriginalOrigin
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingLineOfBusinessComposer
import org.pillarone.riskanalytics.core.components.MultipleCalculationPhaseComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.domain.pc.claims.MarketClaimsMerger
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ConfigurableLob extends MultipleCalculationPhaseComposedComponent implements ISegmentMarker {

    PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList(UnderwritingInfo.class);
    PacketList<CededUnderwritingInfo> inUnderwritingInfoCeded = new PacketList(CededUnderwritingInfo.class);
    PacketList<Claim> inClaimsGross = new PacketList(Claim.class);
    PacketList<Claim> inClaimsCeded = new PacketList(Claim.class);

    PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);
    PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    PacketList<CededUnderwritingInfo> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfo>(CededUnderwritingInfo.class);

    MarketToLineOfBusinessClaims subClaimsFilter;
    UnderwritingLineOfBusinessComposer subUnderwritingFilter;

    ClaimsFilterByOriginalOrigin subClaimsFilterCeded;
    MarketClaimsMerger subClaimsAggregator;
    UnderwritingFilterByOriginalOrigin subUnderwritingInfoFilterCeded;
    UnderwritingInfoAggregator subUnderwritingInfoAggregator;

    ConfigurableLob() {
        subClaimsFilter = new MarketToLineOfBusinessClaims();
        subUnderwritingFilter = new UnderwritingLineOfBusinessComposer();
        subClaimsFilterCeded = new ClaimsFilterByOriginalOrigin();
        subClaimsAggregator = new MarketClaimsMerger();
        subUnderwritingInfoFilterCeded = new UnderwritingFilterByOriginalOrigin();
        subUnderwritingInfoAggregator = new UnderwritingInfoAggregator();
    }

    public void wire() {
        WiringUtils.use(WireCategory) {
            subUnderwritingInfoAggregator.inUnderwritingInfoGross = subUnderwritingFilter.outUnderwritingInfo
            subClaimsFilterCeded.inClaimsGross = subClaimsFilter.outClaims
            subClaimsAggregator.inClaimsGross = subClaimsFilter.outClaims
            subClaimsAggregator.inClaimsCeded = subClaimsFilterCeded.outClaims
            subUnderwritingInfoFilterCeded.inUnderwritingInfoGross = subUnderwritingFilter.outUnderwritingInfo
            subUnderwritingInfoAggregator.inUnderwritingInfoCeded = subUnderwritingInfoFilterCeded.outUnderwritingInfo
        }
        WiringUtils.use(PortReplicatorCategory) {
            subUnderwritingFilter.inUnderwritingInfo = this.inUnderwritingInfoGross
            subClaimsFilter.inClaims = this.inClaimsGross

            this.outUnderwritingInfoGross = subUnderwritingFilter.outUnderwritingInfo
            this.outClaimsGross = subClaimsFilter.outClaims

            subUnderwritingInfoFilterCeded.inUnderwritingInfoCeded = this.inUnderwritingInfoCeded
            subClaimsFilterCeded.inClaimsCeded = this.inClaimsCeded

            this.outUnderwritingInfoCeded = subUnderwritingInfoAggregator.outUnderwritingInfoCeded
            this.outUnderwritingInfoNet = subUnderwritingInfoAggregator.outUnderwritingInfoNet
            this.outClaimsCeded = subClaimsAggregator.outClaimsCeded
            this.outClaimsNet = subClaimsAggregator.outClaimsNet
        }
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inUnderwritingInfoGross, MultipleCalculationPhaseComposedComponent.PHASE_START);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, MultipleCalculationPhaseComposedComponent.PHASE_START);
        setTransmitterPhaseInput(inClaimsGross, MultipleCalculationPhaseComposedComponent.PHASE_START);
        setTransmitterPhaseOutput(outClaimsGross, MultipleCalculationPhaseComposedComponent.PHASE_START);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
        setTransmitterPhaseInput(inClaimsCeded, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
        setTransmitterPhaseOutput(outClaimsCeded, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
        setTransmitterPhaseOutput(outClaimsNet, MultipleCalculationPhaseComposedComponent.PHASE_DO_CALCULATION);
    }
}
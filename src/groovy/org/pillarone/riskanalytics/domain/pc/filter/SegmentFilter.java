package org.pillarone.riskanalytics.domain.pc.filter;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This components filters incoming packets and multiplies them by the portion specified in parmPortions,
 * if their lineOfBusiness property corresponds to one of the selected segments in parmPortions.
 * Each channel is treated individually. The component does not try to establish a relation between gross,
 * ceded and net channel.<br/>
 * Incoming claims have to be of type ClaimDevelopmentLeanPacket. Other claim types would lead to a
 * java.lang.ClassCastException.
 * <p/>
 * Original specification: https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1031
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SegmentFilter extends Component {

    private static final String SEGMENT = "Segment";
    private static final String PORTION = "Portion";

    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsNet = new PacketList<Claim>(Claim.class);

    private PacketList<ClaimDevelopmentLeanPacket> outClaimsNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

//    private String parmSegmentFilterCategory;
    private ConstrainedMultiDimensionalParameter parmPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(SEGMENT, PORTION),
            ConstraintsFactory.getConstraints(SegmentPortion.IDENTIFIER));

    private List<ISegmentMarker> segments;
    private List<Double> portions = new ArrayList<Double>();


    @Override
    protected void doCalculation() {

        segments = parmPortions.getColumnByName(SEGMENT);
        int indexPortions = parmPortions.getColumnIndex(PORTION);
        for (int i = parmPortions.getTitleRowCount(); i < parmPortions.getRowCount(); i++) {
            portions.add(InputFormatConverter.getDouble(parmPortions.getValueAt(i, indexPortions)));
        }
        if (segments.size() > 0) {
            filterSegmentClaims(inClaimsGross, outClaimsGross);
            filterSegmentClaims(inClaimsCeded, outClaimsCeded);
            filterSegmentClaims(inClaimsNet, outClaimsNet);
            filterSegmentUnderwritingInfo(inUnderwritingInfoGross, outUnderwritingInfoGross);
            filterSegmentUnderwritingInfo(inUnderwritingInfoCeded, outUnderwritingInfoCeded);
            filterSegmentUnderwritingInfo(inUnderwritingInfoNet, outUnderwritingInfoNet);
        }
    }

    private void filterSegmentClaims(PacketList<Claim> allClaims, PacketList<ClaimDevelopmentLeanPacket> filterClaims) {
        if (isSenderWired(filterClaims)) {
            for (Claim claim : allClaims) {
                int row = segments.indexOf(claim.getLineOfBusiness().getNormalizedName());
                if (row > -1) {
                    double portion = InputFormatConverter.getDouble(portions.get(row));
                    ClaimDevelopmentLeanPacket segmentClaim = (ClaimDevelopmentLeanPacket) claim.copy();
                    segmentClaim.scale(portion);
                    filterClaims.add(segmentClaim);
                }
            }
        }
    }

    private void filterSegmentUnderwritingInfo(PacketList<UnderwritingInfo> allUnderwritingInfo,
                                               PacketList<UnderwritingInfo> filterUnderwritingInfo) {
        if (isSenderWired(filterUnderwritingInfo)) {
            for (UnderwritingInfo underwritingInfo : allUnderwritingInfo) {
                int row = segments.indexOf(underwritingInfo.getLineOfBusiness().getNormalizedName());
                if (row > -1) {
                    double portion = InputFormatConverter.getDouble(portions.get(row));
                    UnderwritingInfo segmentUnderwritingInfo = underwritingInfo.copy();
                    segmentUnderwritingInfo.scale(portion);
                    filterUnderwritingInfo.add(segmentUnderwritingInfo);
                }
            }
        }
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoGross() {
        return inUnderwritingInfoGross;
    }


    public void setInUnderwritingInfoGross(PacketList<UnderwritingInfo> inUnderwritingInfoGross) {
        this.inUnderwritingInfoGross = inUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<UnderwritingInfo> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfo> getInUnderwritingInfoNet() {
        return inUnderwritingInfoNet;
    }

    public void setInUnderwritingInfoNet(PacketList<UnderwritingInfo> inUnderwritingInfoNet) {
        this.inUnderwritingInfoNet = inUnderwritingInfoNet;
    }

    public PacketList<Claim> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<Claim> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<Claim> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<Claim> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<Claim> getInClaimsNet() {
        return inClaimsNet;
    }

    public void setInClaimsNet(PacketList<Claim> inClaimsNet) {
        this.inClaimsNet = inClaimsNet;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<ClaimDevelopmentLeanPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimDevelopmentLeanPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<ClaimDevelopmentLeanPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfo> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfo> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<UnderwritingInfo> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public ConstrainedMultiDimensionalParameter getParmPortions() {
        return parmPortions;
    }

    public void setParmPortions(ConstrainedMultiDimensionalParameter parmPortions) {
        this.parmPortions = parmPortions;
    }
}

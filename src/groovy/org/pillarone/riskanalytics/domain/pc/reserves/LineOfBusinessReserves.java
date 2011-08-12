package org.pillarone.riskanalytics.domain.pc.reserves;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.SortClaimsByFractionOfPeriod;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"SEGMENT"})
public class LineOfBusinessReserves extends Component {
    private static final String RESERVES = "Reserves";
    private static final String PORTION = "Portion of Claims";

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<SingleValuePacket> inInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    private PacketList<SingleValuePacket> outInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLean = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private ConstrainedMultiDimensionalParameter parmPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"),
            Arrays.asList(RESERVES, PORTION),
            ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER));


    protected void doCalculation() {
        if (inClaims.size() > 0) {
            List<Claim> lobClaims = new ArrayList<Claim>();
            int portionColumn = parmPortions.getColumnIndex(PORTION);
            Component lineOfBusiness = inClaims.get(0).sender; // works only if this component is part of a component implementing ISegmentMarker
            for (Claim claim : inClaims) {
                String originName = claim.origin.getNormalizedName();
                int row = parmPortions.getColumnByName(RESERVES).indexOf(originName);
                Claim lobClaim = claim.copy();
                lobClaim.setOriginalClaim(lobClaim);
                lobClaim.origin = lineOfBusiness;
                lobClaim.addMarker(ISegmentMarker.class, (IComponentMarker) lineOfBusiness);
                lobClaim.scale(InputFormatConverter.getDouble(parmPortions.getValueAt(row + 1, portionColumn)));
                lobClaims.add(lobClaim);
                outClaimsDevelopmentLean.add((ClaimDevelopmentLeanPacket) lobClaim);
            }
            for (SingleValuePacket initialReserves : inInitialReserves) {
                String originName = initialReserves.origin.getNormalizedName();
                int row = parmPortions.getColumnByName(RESERVES).indexOf(originName);
                SingleValuePacket lobInitialReserve = (SingleValuePacket) initialReserves.copy();
                lobInitialReserve.origin = lineOfBusiness;
                lobInitialReserve.value *= InputFormatConverter.getDouble(parmPortions.getValueAt(row + 1, portionColumn));
                outInitialReserves.add(lobInitialReserve);
            }
            Collections.sort(lobClaims, SortClaimsByFractionOfPeriod.getInstance());
            outClaims.addAll(lobClaims);
        }
    }

    @Override
    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel == inClaims) {
            if (source.size() > 0 && parmPortions.getRowCount() - parmPortions.getTitleRowCount() > 0) {
                for (Object claim : source) {
                    String originName = ((Packet) claim).origin.getNormalizedName();
                    int row = parmPortions.getColumnByName(RESERVES).indexOf(originName);
                    if (row > -1 && ((Claim) claim).getPeril() instanceof IReserveMarker) {
                        inClaims.add((Claim) claim);
                    }
                }
            }
        }
        else if (inChannel == inInitialReserves) {
            if (source.size() > 0 && parmPortions.getRowCount() - parmPortions.getTitleRowCount() > 0) {
                for (Object initialReserves : source) {
                    String originName = ((Packet) initialReserves).origin.getNormalizedName();
                    int row = parmPortions.getColumnByName(RESERVES).indexOf(originName);
                    if (row > -1) {
                        inInitialReserves.add((SingleValuePacket) initialReserves);
                    }
                }
            }
        }
        else {
            super.filterInChannel(inChannel, source);
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

    public ConstrainedMultiDimensionalParameter getParmPortions() {
        return parmPortions;
    }

    public void setParmPortions(ConstrainedMultiDimensionalParameter parmPortions) {
        this.parmPortions = parmPortions;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsDevelopmentLean() {
        return outClaimsDevelopmentLean;
    }

    public void setOutClaimsDevelopmentLean(PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLean) {
        this.outClaimsDevelopmentLean = outClaimsDevelopmentLean;
    }

    public PacketList<SingleValuePacket> getInInitialReserves() {
        return inInitialReserves;
    }

    public void setInInitialReserves(PacketList<SingleValuePacket> inInitialReserves) {
        this.inInitialReserves = inInitialReserves;
    }

    public PacketList<SingleValuePacket> getOutInitialReserves() {
        return outInitialReserves;
    }

    public void setOutInitialReserves(PacketList<SingleValuePacket> outInitialReserves) {
        this.outInitialReserves = outInitialReserves;
    }
}

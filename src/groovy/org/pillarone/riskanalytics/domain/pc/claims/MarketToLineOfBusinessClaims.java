package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.constraints.PerilPortion;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MarketToLineOfBusinessClaims extends Component {

    private static final String PERIL = "Claims Generator";
    private static final String PORTION = "Portion of Claims";

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLean = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private ConstrainedMultiDimensionalParameter parmPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{"", 1d}),
            Arrays.asList(PERIL, PORTION),
            ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER));


    protected void doCalculation() {
        if (inClaims.size() > 0) {
            List<Claim> lobClaims = new ArrayList<Claim>();
            int portionColumn = parmPortions.getColumnIndex(PORTION);
            Component lineOfBusiness = inClaims.get(0).sender;
            if (!(lineOfBusiness instanceof LobMarker)) {
                throw new IllegalArgumentException("MarketToLineOfBusinessClaims component may be used only within a line of business component");
            }
            for (Claim marketClaim : inClaims) {
                String originName = marketClaim.origin.getNormalizedName();
                int row = parmPortions.getColumnByName(PERIL).indexOf(originName);
                if (row > -1) {
                    Claim lobClaim = marketClaim.copy();
                    // PMO-750: claim mergers in reinsurance program won't work with reference to market claims
                    lobClaim.setOriginalClaim(lobClaim);
                    lobClaim.origin = lineOfBusiness;
                    lobClaim.setLineOfBusiness((LobMarker) lineOfBusiness);
                    lobClaim.scale((Double) parmPortions.getValueAt(row + 1, portionColumn));
                    lobClaims.add(lobClaim);
                    if (lobClaim instanceof ClaimDevelopmentLeanPacket) {
                        outClaimsDevelopmentLean.add((ClaimDevelopmentLeanPacket) lobClaim);
                    }
                }
            }
            Collections.sort(lobClaims, SortClaimsByFractionOfPeriod.getInstance());
            outClaims.addAll(lobClaims);
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
}
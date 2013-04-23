package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM"})
public class MarketToLineOfBusinessClaims extends Component {

    private static final String PERIL = "Claims Generator";
    private static final String PORTION = "Portion of Claims";

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    private ConstrainedMultiDimensionalParameter parmPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(PERIL, PORTION),ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER));

    private Map<IPerilMarker, Double> perilWeights;


    protected void doCalculation() {
        if (inClaims.size() > 0) {
            List<Claim> lobClaims = new ArrayList<Claim>();
            Component lineOfBusiness = inClaims.get(0).sender;
            if (!(lineOfBusiness instanceof ISegmentMarker)) {
                throw new IllegalArgumentException("MarketToLineOfBusinessClaims.componentMismatch");
            }
            for (Claim marketClaim : inClaims) {
                Claim lobClaim = marketClaim.copy();
                // PMO-750: claim mergers in reinsurance program won't work with reference to market claims
                lobClaim.setOriginalClaim(lobClaim);
                lobClaim.origin = lineOfBusiness;
                lobClaim.addMarker(ISegmentMarker.class, (IComponentMarker) lineOfBusiness);
                IPerilMarker peril = (IPerilMarker) marketClaim.getMarkedSender(IPerilMarker.class);
                lobClaim.scale(perilWeights.get(peril));
                lobClaims.add(lobClaim);
            }
            Collections.sort(lobClaims, SortClaimsByFractionOfPeriod.getInstance());
            outClaims.addAll(lobClaims);
        }
    }

    @Override
    public void filterInChannel(PacketList inChannel, PacketList source) {
        initPerilWeights();
        if (inChannel == inClaims) {
            if (source.size() > 0 && parmPortions.getRowCount() - parmPortions.getTitleRowCount() > 0) {
                List<IPerilMarker> selectedPerils = parmPortions.getValuesAsObjects(0);
                for (Object claim : source) {
                    IPerilMarker peril = (IPerilMarker) ((Packet) claim).getMarkedSender(IPerilMarker.class);
                    if (selectedPerils.contains(peril)) {
                        inClaims.add((Claim) claim);
                    }
                }
            }
        }
        else {
            super.filterInChannel(inChannel, source);
        }
    }

    private void initPerilWeights() {
        if (perilWeights == null) {
            perilWeights = new HashMap<IPerilMarker, Double>();
            int portionColumnIndex = parmPortions.getColumnIndex(PORTION);
            int perilColumnIndex = parmPortions.getColumnIndex(PERIL);
            for (int row = parmPortions.getTitleRowCount(); row < parmPortions.getRowCount(); row++) {
                Double weight = InputFormatConverter.getDouble(parmPortions.getValueAt(row, portionColumnIndex));
                IPerilMarker peril = (IPerilMarker) parmPortions.getValueAtAsObject(row, perilColumnIndex);
                perilWeights.put(peril, weight);
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

    public ConstrainedMultiDimensionalParameter getParmPortions() {
        return parmPortions;
    }

    public void setParmPortions(ConstrainedMultiDimensionalParameter parmPortions) {
        this.parmPortions = parmPortions;
    }
}
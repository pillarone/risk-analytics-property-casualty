package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.Arrays;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsFilterByPeril extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    private ComboBoxTableMultiDimensionalParameter parmPerils = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(new String[]{""}),
            Arrays.asList(new String[]{"perils"}),
            IPerilMarker.class);


    public void doCalculation() {
        outClaims.addAll(ClaimFilterUtilities.filterClaims(inClaims, parmPerils.getValuesAsObjects()));
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

    public ComboBoxTableMultiDimensionalParameter getParmPerils() {
        return parmPerils;
    }

    public void setParmPerils(ComboBoxTableMultiDimensionalParameter parmPerils) {
        this.parmPerils = parmPerils;
    }
}
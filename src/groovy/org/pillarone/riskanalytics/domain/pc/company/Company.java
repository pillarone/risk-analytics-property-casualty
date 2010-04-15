package org.pillarone.riskanalytics.domain.pc.company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.lob.CompanyConfigurableLobWithReserves;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Company extends Component implements ICompanyMarker {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    private double parmToBeRemoved = 0;

    private static Log LOG = LogFactory.getLog(Company.class);

    @Override
    protected void doCalculation() {
        for (Claim claim : inClaims) {
            LobMarker lob = claim.getLineOfBusiness();
            if (lob instanceof CompanyConfigurableLobWithReserves) {
                if (((CompanyConfigurableLobWithReserves) lob).getParmCompany().getSelectedComponent() == this) {
                    outClaims.add(claim);
                }
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

    public double getParmToBeRemoved() {
        return parmToBeRemoved;
    }

    public void setParmToBeRemoved(double parmToBeRemoved) {
        this.parmToBeRemoved = parmToBeRemoved;
    }
}

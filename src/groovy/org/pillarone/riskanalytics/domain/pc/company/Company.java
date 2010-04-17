package org.pillarone.riskanalytics.domain.pc.company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.lob.CompanyConfigurableLobWithReserves;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCompanyCoverAttributeReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;

import java.util.HashMap;
import java.util.Map;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Company extends Component implements ICompanyMarker {

    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private double parmToBeRemoved = 0;

    private static Log LOG = LogFactory.getLog(Company.class);
    /**
     * contains the covered portion of <tt>this</tt> company per contract
     */
    private Map<IReinsuranceContractMarker, Double> coveredPortionPerContract = new HashMap<IReinsuranceContractMarker, Double>();

    @Override
    protected void doCalculation() {
        try {
            Claim aggregateClaimGross;
            Claim aggregateClaimCeded;
            if (inClaimsGross.size() > 0) {
                aggregateClaimGross = inClaimsGross.get(0).getClass().newInstance();
                aggregateClaimCeded = inClaimsGross.get(0).getClass().newInstance();
            }
            else {
                aggregateClaimGross = (Claim) inClaimsGross.getType().newInstance();
                aggregateClaimCeded = (Claim) inClaimsGross.getType().newInstance();
            }
            for (Claim claim : inClaimsGross) {
                // the line of business belongs to this company and therefore the gross claim is added to this company
                if (isCompanyClaim(claim)) {
                    aggregateClaimGross.plus(claim);
                }
            }

            for (Claim claim : inClaimsCeded) {
                IReinsuranceContractMarker contract = claim.getReinsuranceContract();
                if (isCompanyClaim(claim)) {
                    aggregateClaimCeded.plus(claim);
                }
                // check if this company was the reinsurer of a ceded claim, if true add the claim to the gross claim list
                else if (contract instanceof MultiCompanyCoverAttributeReinsuranceContract) {
                    Claim coveredClaimByThisCompany = claim.copy();
                    coveredClaimByThisCompany.scale(getCoveredPortion(contract));
                    aggregateClaimGross.plus(coveredClaimByThisCompany);
                }
            }

            outClaimsGross.add(aggregateClaimGross);
            outClaimsCeded.add(aggregateClaimCeded);
            Claim aggregateClaimNet = aggregateClaimGross.copy();
            aggregateClaimNet.minus(aggregateClaimCeded);
            outClaimsNet.add(aggregateClaimNet);
            if (aggregateClaimGross instanceof ClaimDevelopmentLeanPacket) {
                outClaimsLeanDevelopmentGross.add((ClaimDevelopmentLeanPacket) aggregateClaimGross);
                outClaimsLeanDevelopmentCeded.add((ClaimDevelopmentLeanPacket) aggregateClaimCeded);
                outClaimsLeanDevelopmentNet.add((ClaimDevelopmentLeanPacket) aggregateClaimNet);
            }

            // underwriting info calculations:
            UnderwritingInfo aggregatedUnderwritingInfoGross;
            UnderwritingInfo aggregateUnderwritingInfoCeded;
            if (inUnderwritingInfoGross.size() > 0) {
                aggregatedUnderwritingInfoGross = inUnderwritingInfoGross.get(0).getClass().newInstance();
                aggregateUnderwritingInfoCeded = inUnderwritingInfoGross.get(0).getClass().newInstance();
            }
            else {
                aggregatedUnderwritingInfoGross = (UnderwritingInfo) inUnderwritingInfoGross.getType().newInstance();
                aggregateUnderwritingInfoCeded = (UnderwritingInfo) inUnderwritingInfoGross.getType().newInstance();
            }
            for (UnderwritingInfo underwritingInfo : inUnderwritingInfoGross) {
                // the line of business belongs to this company and therefore the gross underwriting info is added to this company
                if (isCompanyUnderwritingInfo(underwritingInfo)) {
                    aggregatedUnderwritingInfoGross.plus(underwritingInfo);
                }
            }

            for (UnderwritingInfo underwritingInfo : inUnderwritingInfoCeded) {
                IReinsuranceContractMarker contract = underwritingInfo.getReinsuranceContract();
                if (isCompanyUnderwritingInfo(underwritingInfo)) {
                    aggregateUnderwritingInfoCeded.plus(underwritingInfo);
                }
                // check if this company was the reinsurer of a ceded underwriting info, if true add up the underwriting info to the aggregate gross
                else if (contract instanceof MultiCompanyCoverAttributeReinsuranceContract) {
                    UnderwritingInfo underwritingInfoOfThisCompany = (UnderwritingInfo) underwritingInfo.copy();
                    underwritingInfoOfThisCompany.scale(getCoveredPortion(contract));
                    aggregatedUnderwritingInfoGross.plus(underwritingInfoOfThisCompany);
                }
            }

            outUnderwritingInfoGross.add(aggregatedUnderwritingInfoGross);
            outUnderwritingInfoCeded.add(aggregateUnderwritingInfoCeded);
            UnderwritingInfo aggregateUnderwritingInfoNet = (UnderwritingInfo) aggregatedUnderwritingInfoGross.copy();
            aggregateUnderwritingInfoNet.minus(aggregateUnderwritingInfoCeded);
            outUnderwritingInfoNet.add(aggregateUnderwritingInfoNet);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * @param claim
     * @return if the claim's line of business belongs to this company
     */
    private boolean isCompanyClaim(Claim claim) {
        LobMarker lob = claim.getLineOfBusiness();
        return (lob instanceof CompanyConfigurableLobWithReserves) &&
                (((CompanyConfigurableLobWithReserves) lob).getParmCompany().getSelectedComponent() == this);
    }
    /**
     * @param underwritingInfo
     * @return if the underwriting info's line of business belongs to this company
     */
    private boolean isCompanyUnderwritingInfo(UnderwritingInfo underwritingInfo) {
        LobMarker lob = underwritingInfo.getLineOfBusiness();
        return (lob instanceof CompanyConfigurableLobWithReserves) &&
                (((CompanyConfigurableLobWithReserves) lob).getParmCompany().getSelectedComponent() == this);
    }

    private Double getCoveredPortion(IReinsuranceContractMarker contract) {
        Double portion = coveredPortionPerContract.get(contract);
        if (portion == null) {
            if (!(contract instanceof MultiCompanyCoverAttributeReinsuranceContract)) {
                return 0d;
            }
            ConstrainedMultiDimensionalParameter coverPortions = ((MultiCompanyCoverAttributeReinsuranceContract) contract).getParmReinsurers();
            int numberOfReinsurers = coverPortions.getValueRowCount();
            int firstRowWithReinsurer = coverPortions.getTitleRowCount();
            for (int row = firstRowWithReinsurer; row <= numberOfReinsurers; row++) {
                String reinsurerName = (String) coverPortions.getValueAt(row, 1);
                portion = (Double) coverPortions.getValueAt(row, 2);
                if (!reinsurerName.equals(this.getNormalizedName())) {
                    // this company is not a reinsurer for the contract
                    portion = 0d;
                }
                coveredPortionPerContract.put(contract, portion);
            }
        }
        return portion;
    }

    @Override
    public String toString() {
        return this.getNormalizedName();
    }

    public PacketList<Claim> getInClaimsGross() {
        return inClaimsGross;
    }

    public void setInClaimsGross(PacketList<Claim> inClaimsGross) {
        this.inClaimsGross = inClaimsGross;
    }

    public PacketList<Claim> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<Claim> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public double getParmToBeRemoved() {
        return parmToBeRemoved;
    }

    public void setParmToBeRemoved(double parmToBeRemoved) {
        this.parmToBeRemoved = parmToBeRemoved;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentGross() {
        return outClaimsLeanDevelopmentGross;
    }

    public void setOutClaimsLeanDevelopmentGross(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGross) {
        this.outClaimsLeanDevelopmentGross = outClaimsLeanDevelopmentGross;
    }

    public PacketList<Claim> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<Claim> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<Claim> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<Claim> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<Claim> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<Claim> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentCeded() {
        return outClaimsLeanDevelopmentCeded;
    }

    public void setOutClaimsLeanDevelopmentCeded(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentCeded) {
        this.outClaimsLeanDevelopmentCeded = outClaimsLeanDevelopmentCeded;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentNet() {
        return outClaimsLeanDevelopmentNet;
    }

    public void setOutClaimsLeanDevelopmentNet(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentNet) {
        this.outClaimsLeanDevelopmentNet = outClaimsLeanDevelopmentNet;
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

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfo> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }
}

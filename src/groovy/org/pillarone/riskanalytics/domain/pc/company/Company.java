package org.pillarone.riskanalytics.domain.pc.company;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.assets.constants.Rating;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.lob.CompanyConfigurableLobWithReserves;
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCompanyCoverAttributeReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities;

import java.util.HashMap;
import java.util.Map;


/**
 * Class company is a component of the MultiCompany-model to obtain aggregated claims and
 * underwriting information associated to the instantiated company.
 * <p/>
 * The incoming information is filtered with respect to engaged companies,
 * matches with the instance company entail a modification of its aggregated portfolio.
 * Attention should be paid to the case where one of the reinsurers of a ceded claim or underwriting information
 * equals the instance company. This situation leads to a (portion-weighted) increase of the aggregated gross
 * claim, respectively underwriting information. The incoming claims (analogously
 * incoming underwriting information) are filtered by means of the parameters parmCompany and parmReinsurers
 * provided by the components lineOfBusiness and reinsuranceContract.
 * <p/>
 * Implementation Note: For the query algorithm to correctly function, it is crucial that the components line of
 * business and reinsurance contract are instances of classes CompanyConfigurableLobWithReserves and
 * MultiCompanyCoverAttributeReinsuranceContract, respectively.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Company extends Component implements ICompanyMarker {

    private PacketList<Claim> inClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> inClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> inUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);

    private PacketList<Claim> outClaimsGross = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGrossPrimaryInsurer = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsGrossReinsurer = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsCeded = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNet = new PacketList<Claim>(Claim.class);
    private PacketList<Claim> outClaimsNetPrimaryInsurer = new PacketList<Claim>(Claim.class);

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGross = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGrossPrimaryInsurer = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGrossReinsurer = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentCeded = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentNet = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentNetPrimaryInsurer = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGross = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGrossPrimaryInsurer = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoGrossReinsurer = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoCeded = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNet = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    private PacketList<UnderwritingInfo> outUnderwritingInfoNetPrimaryInsurer = new PacketList<UnderwritingInfo>(UnderwritingInfo.class);
    /**
     * This parameter is currently not used for any calculation. It may be used for default modeling as in DCEM.
     * Reason for adding it: Components addable in a DynamicComposedComponent need at least one parameter.
     */
    private Rating parmRating = Rating.BBB;

    /**
     * contains the covered portion of <tt>this</tt> company per contract
     */
    private Map<IReinsuranceContractMarker, Double> coveredPortionPerContract = new HashMap<IReinsuranceContractMarker, Double>();

    @Override
    protected void doCalculation() {
        try {
            Claim aggregateClaimGrossPI;
            Claim aggregateClaimCeded;
            Claim aggregateClaimGrossRI;
            if (inClaimsGross.size() > 0) {
                aggregateClaimGrossPI = inClaimsGross.get(0).getClass().newInstance();
                aggregateClaimCeded = inClaimsGross.get(0).getClass().newInstance();
                aggregateClaimGrossRI = inClaimsGross.get(0).getClass().newInstance();
            } else {
                aggregateClaimGrossPI = (Claim) inClaimsGross.getType().newInstance();
                aggregateClaimCeded = (Claim) inClaimsGross.getType().newInstance();
                aggregateClaimGrossRI = (Claim) inClaimsGross.getType().newInstance();
            }
            for (Claim claim : inClaimsGross) {
                // the line of business belongs to this company and therefore the gross claim is added to this company
                if (isCompanyClaim(claim)) {
                    aggregateClaimGrossPI.plus(claim);
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
                    aggregateClaimGrossRI.plus(coveredClaimByThisCompany);
                }
            }

            outClaimsGrossPrimaryInsurer.add(aggregateClaimGrossPI);
            outClaimsCeded.add(aggregateClaimCeded);
            outClaimsGrossReinsurer.add(aggregateClaimGrossRI);
            Claim aggregateClaimGross = aggregateClaimGrossPI.copy();
            aggregateClaimGross.plus(aggregateClaimGrossRI);
            outClaimsGross.add(aggregateClaimGross);
            Claim aggregateClaimNet = aggregateClaimGross.copy();
            aggregateClaimNet.minus(aggregateClaimCeded);
            outClaimsNet.add(aggregateClaimNet);
            Claim aggregateClaimNetPI = aggregateClaimGrossPI.copy();
            aggregateClaimNetPI.minus(aggregateClaimCeded);
            outClaimsNetPrimaryInsurer.add(aggregateClaimNetPI);

            if (aggregateClaimGross instanceof ClaimDevelopmentLeanPacket) {
                outClaimsLeanDevelopmentGrossPrimaryInsurer.add((ClaimDevelopmentLeanPacket) aggregateClaimGrossPI);
                outClaimsLeanDevelopmentGrossReinsurer.add((ClaimDevelopmentLeanPacket) aggregateClaimGrossRI);
                outClaimsLeanDevelopmentGross.add((ClaimDevelopmentLeanPacket) aggregateClaimGross);
                outClaimsLeanDevelopmentCeded.add((ClaimDevelopmentLeanPacket) aggregateClaimCeded);
                outClaimsLeanDevelopmentNet.add((ClaimDevelopmentLeanPacket) aggregateClaimNet);
                outClaimsLeanDevelopmentNetPrimaryInsurer.add((ClaimDevelopmentLeanPacket) aggregateClaimNetPI);
            }

            // underwriting info calculations:
            UnderwritingInfo aggregatedUnderwritingInfoGrossPI;
            UnderwritingInfo aggregatedUnderwritingInfoGrossRI;
            UnderwritingInfo aggregatedUnderwritingInfoCeded;
            if (inUnderwritingInfoGross.size() > 0) {
                aggregatedUnderwritingInfoGrossPI = inUnderwritingInfoGross.get(0).getClass().newInstance();
                aggregatedUnderwritingInfoGrossRI = inUnderwritingInfoGross.get(0).getClass().newInstance();
                aggregatedUnderwritingInfoCeded = inUnderwritingInfoGross.get(0).getClass().newInstance();
            } else {
                aggregatedUnderwritingInfoGrossPI = (UnderwritingInfo) inUnderwritingInfoGross.getType().newInstance();
                aggregatedUnderwritingInfoGrossRI = (UnderwritingInfo) inUnderwritingInfoGross.getType().newInstance();
                aggregatedUnderwritingInfoCeded = (UnderwritingInfo) inUnderwritingInfoGross.getType().newInstance();
            }
            for (UnderwritingInfo underwritingInfo : inUnderwritingInfoGross) {
                // the line of business belongs to this company and therefore the gross underwriting info is added to this company
                if (isCompanyUnderwritingInfo(underwritingInfo)) {
                    aggregatedUnderwritingInfoGrossPI.plus(underwritingInfo);
                }
            }

            for (UnderwritingInfo underwritingInfo : inUnderwritingInfoCeded) {
                IReinsuranceContractMarker contract = underwritingInfo.getReinsuranceContract();
                if (isCompanyUnderwritingInfo(underwritingInfo)) {
                    aggregatedUnderwritingInfoCeded.plus(underwritingInfo);
                }
                // check if this company was the reinsurer of a ceded underwriting info, if true add up the underwriting info to the aggregate gross
                else if (contract instanceof MultiCompanyCoverAttributeReinsuranceContract) {
                    UnderwritingInfo underwritingInfoOfThisCompany = (UnderwritingInfo) underwritingInfo.copy();
                    underwritingInfoOfThisCompany.scale(getCoveredPortion(contract));
                    aggregatedUnderwritingInfoGrossRI.plus(underwritingInfoOfThisCompany);
                }
            }

            outUnderwritingInfoGrossPrimaryInsurer.add(aggregatedUnderwritingInfoGrossPI);
            outUnderwritingInfoGrossReinsurer.add(aggregatedUnderwritingInfoGrossRI);
            outUnderwritingInfoCeded.add(aggregatedUnderwritingInfoCeded);
            UnderwritingInfo aggregatedUnderwritingInfoGross = (UnderwritingInfo) aggregatedUnderwritingInfoGrossPI.copy();
            aggregatedUnderwritingInfoGross.plus(aggregatedUnderwritingInfoGrossRI);
            outUnderwritingInfoGross.add(aggregatedUnderwritingInfoGross);
            UnderwritingInfo aggregatedUnderwritingInfoNet = UnderwritingInfoUtilities.calculateNet(aggregatedUnderwritingInfoGross, aggregatedUnderwritingInfoCeded);
            outUnderwritingInfoNet.add(aggregatedUnderwritingInfoNet);
            UnderwritingInfo aggregatedUnderwritingInfoNetPI = UnderwritingInfoUtilities.calculateNet(aggregatedUnderwritingInfoGrossPI, aggregatedUnderwritingInfoCeded);
            outUnderwritingInfoNetPrimaryInsurer.add(aggregatedUnderwritingInfoNetPI);
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

    /**
     * @param contract
     * @return is only executed if the reinsurance contract is not yet collected by the map.
     *         The algorithm compares each reinsurer in the list with the company. It stops with the first matching, returns
     *         the corresponding portion and saves contract and portion in the map coveredPortionperContract for further
     *         queries. If no matches were found, portion is set to zero.
     */
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
                if (portion > 0) break;   // stop, if equal names were found
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

    public PacketList<Claim> getOutClaimsGrossPrimaryInsurer() {
        return outClaimsGrossPrimaryInsurer;
    }

    public void setOutClaimsGrossPrimaryInsurer(PacketList<Claim> outClaimsGrossPrimaryInsurer) {
        this.outClaimsGrossPrimaryInsurer = outClaimsGrossPrimaryInsurer;
    }

    public PacketList<Claim> getOutClaimsGrossReinsurer() {
        return outClaimsGrossReinsurer;
    }
    public void setOutClaimsGrossReinsurer(PacketList<Claim> outClaimsGrossReinsurer) {
        this.outClaimsGrossReinsurer = outClaimsGrossReinsurer;
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

    public PacketList<Claim> getOutClaimsNetPrimaryInsurer() {
        return outClaimsNetPrimaryInsurer;
    }

    public void setOutClaimsNetPrimaryInsurer(PacketList<Claim> outClaimsNetPrimaryInsurer) {
        this.outClaimsNetPrimaryInsurer = outClaimsNetPrimaryInsurer;
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

    public Rating getParmRating() {
        return parmRating;
    }

    public void setParmRating(Rating parmRating) {
        this.parmRating = parmRating;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoGrossPrimaryInsurer() {
        return outUnderwritingInfoGrossPrimaryInsurer;
    }

    public void setOutUnderwritingInfoGrossPrimaryInsurer(PacketList<UnderwritingInfo> outUnderwritingInfoGrossPrimaryInsurer) {
        this.outUnderwritingInfoGrossPrimaryInsurer = outUnderwritingInfoGrossPrimaryInsurer;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoGrossReinsurer() {
        return outUnderwritingInfoGrossReinsurer;
    }

    public void setOutUnderwritingInfoGrossReinsurer(PacketList<UnderwritingInfo> outUnderwritingInfoGrossReinsurer) {
        this.outUnderwritingInfoGrossReinsurer = outUnderwritingInfoGrossReinsurer;
    }

    public PacketList<UnderwritingInfo> getOutUnderwritingInfoNetPrimaryInsurer() {
        return outUnderwritingInfoNetPrimaryInsurer;
    }

    public void setOutUnderwritingInfoNetPrimaryInsurer(PacketList<UnderwritingInfo> outUnderwritingInfoNetPrimaryInsurer) {
        this.outUnderwritingInfoNetPrimaryInsurer = outUnderwritingInfoNetPrimaryInsurer;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentGrossPrimaryInsurer() {
        return outClaimsLeanDevelopmentGrossPrimaryInsurer;
    }

    public void setOutClaimsLeanDevelopmentGrossPrimaryInsurer(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGrossPrimaryInsurer) {
        this.outClaimsLeanDevelopmentGrossPrimaryInsurer = outClaimsLeanDevelopmentGrossPrimaryInsurer;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentGrossReinsurer() {
        return outClaimsLeanDevelopmentGrossReinsurer;
    }

    public void setOutClaimsLeanDevelopmentGrossReinsurer(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentGrossReinsurer) {
        this.outClaimsLeanDevelopmentGrossReinsurer = outClaimsLeanDevelopmentGrossReinsurer;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopmentNetPrimaryInsurer() {
        return outClaimsLeanDevelopmentNetPrimaryInsurer;
    }

    public void setOutClaimsLeanDevelopmentNetPrimaryInsurer(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopmentNetPrimaryInsurer) {
        this.outClaimsLeanDevelopmentNetPrimaryInsurer = outClaimsLeanDevelopmentNetPrimaryInsurer;
    }
}

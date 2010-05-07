package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class ClaimDevelopmentPacket extends Claim {

    protected Claim claim;

    private double paid;
    private double reserved;
    private double changeInReserves;

    private Pattern payoutPattern;
    /**
     * negative values correspond to the development period of reserved claims;     // todo(sku): think of using a common definition for pos and neg based on incurred period
     * positive values correspond to the incurred period according to the simulation context
     */
    private int originalPeriod; // according to simulation period

    public ClaimDevelopmentPacket(){
    }

    public ClaimDevelopmentPacket(Claim claim){
        set(claim);
    }

    @Override
    public Claim copy() {
        ClaimDevelopmentPacket copy = ClaimDevelopmentPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the object received as argument are of equal type.
     */
    @Override
    public Claim getNetClaim(Claim cededClaim) {
        ClaimDevelopmentPacket netClaim = (ClaimDevelopmentPacket) copy();
        netClaim.minus(cededClaim);
//        netClaim.setIncurred(netClaim.getIncurred() - ((ClaimDevelopmentPacket) cededClaim).getIncurred());
//        netClaim.paid -= ((ClaimDevelopmentPacket) cededClaim).paid;
//        netClaim.reserved -= ((ClaimDevelopmentPacket) cededClaim).reserved;
        return netClaim;
    }

    public double getIncurredDate() {
        return originalPeriod + getFractionOfPeriod();
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public void plus(Claim claim) {
        setIncurred(getIncurred() + ((ClaimDevelopmentPacket) claim).getIncurred());
        paid += ((ClaimDevelopmentPacket) claim).getPaid();
        reserved += ((ClaimDevelopmentPacket) claim).getReserved();
        changeInReserves += ((ClaimDevelopmentPacket) claim).getChangeInReserves();
//        if (getFractionOfPeriod().equals(claim.getFractionOfPeriod())) {
//            setClaimType(ClaimType.AGGREGATED);
//            setFractionOfPeriod(Math.min(getFractionOfPeriod(), claim.getFractionOfPeriod()));
//        }
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public void minus(Claim claim) {
        setIncurred(getIncurred() - ((ClaimDevelopmentPacket) claim).getIncurred());
        paid -= ((ClaimDevelopmentPacket) claim).getPaid();
        reserved -= ((ClaimDevelopmentPacket) claim).getReserved();
        changeInReserves -= ((ClaimDevelopmentPacket) claim).getChangeInReserves();
//        if (getFractionOfPeriod().equals(claim.getFractionOfPeriod())) {
//            setClaimType(ClaimType.AGGREGATED);
//            setFractionOfPeriod(Math.min(getFractionOfPeriod(), claim.getFractionOfPeriod()));
//        }
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);
        paid *= factor;
        reserved *= factor;
        changeInReserves *= factor;
    }

    /**
     *  Converts the current packet into a Claim packet setting its value to incurred
     */
    public Claim getClaimPacket() {
        if (claim == null) {
            claim = ClaimPacketFactory.createPacket();
            claim.setOrigin(origin);
            claim.setOriginalClaim(getOriginalClaim());
            claim.setEvent(getEvent());
            claim.setFractionOfPeriod(getFractionOfPeriod());
            claim.setClaimType(getClaimType());
            claim.setPeril(getPeril());
            claim.setLineOfBusiness(getLineOfBusiness());
        }
        claim.setUltimate(getUltimate());
        return claim;
    }

    public void set(ClaimDevelopmentPacket claim) {
        super.set(claim);
        setPaid(claim.getPaid());
        setReserved(claim.getReserved());
        setOriginalPeriod(claim.getOriginalPeriod());
        setChangeInReserves(claim.getChangeInReserves());
    }

    protected static final String INCURRED = "incurred";
    protected static final String PAID = "paid";
    protected static final String RESERVED = "reserved";
    protected static final String CHANGE_IN_RESERVES = "change in reserves";

    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>(4);
        valuesToSave.put(INCURRED, getIncurred());
        valuesToSave.put(PAID, paid);
        valuesToSave.put(RESERVED, reserved);
        valuesToSave.put(CHANGE_IN_RESERVES, changeInReserves);
        return valuesToSave;
    }

    @Override
    public List<String> getFieldNames() {
        return Arrays.asList(INCURRED, PAID, RESERVED, CHANGE_IN_RESERVES);
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(getIncurred());
        result.append(separator);
        result.append(paid);
        result.append(separator);
        result.append(reserved);
        result.append(separator);
        if (getClaimType() != null) result.append(getClaimType()).append(separator);
        if (origin != null) result.append(origin.getName()).append(separator);
        if (getOriginalClaim() != null) result.append(System.identityHashCode(getOriginalClaim())).append(separator);
        if (getLineOfBusiness() != null) result.append(getLineOfBusiness().getName()).append(separator);
        if (getPeril() != null) result.append(getPeril().getName()).append(separator);
        if (getReinsuranceContract() != null) result.append(getReinsuranceContract().getName()).append(separator);
        result.append(", original period: ").append(originalPeriod);
        return result.toString();
    }

    public double getIncurred() {
        return getUltimate();
    }

    public void setIncurred(double incurred) {
        setUltimate(incurred);
    }


    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getReserved() {
        return reserved;
    }

    public void setReserved(double reserved) {
        this.reserved = reserved;
    }

    public Pattern getPayoutPattern() {
        return payoutPattern;
    }

    public void setPayoutPattern(Pattern payoutPattern) {
        this.payoutPattern = payoutPattern;
    }

    public int getOriginalPeriod() {
        return originalPeriod;
    }

    public void setOriginalPeriod(int originalPeriod) {
        this.originalPeriod = originalPeriod;
    }

    public double getChangeInReserves() {
        return changeInReserves;
    }

    public void setChangeInReserves(double changeInReserves) {
        this.changeInReserves = changeInReserves;
    }
}
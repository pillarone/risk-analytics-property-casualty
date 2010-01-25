package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/**
 * @author shartmann (at) munichre (dot) com
 */

//todo convert to java class, groovy used to problems during compilation

public class ClaimDevelopmentLeanPacket extends Claim {

    private double paid;
    private double reserved;
    private Claim claim;

    private static final String INCURRED = "incurred";
    private static final String PAID = "paid";
    private static final String RESERVED = "reserved";

    public ClaimDevelopmentLeanPacket(){
    }

    public ClaimDevelopmentLeanPacket(Claim claim){
        set(claim);
    }

    @Override
    public Claim copy() {
        ClaimDevelopmentLeanPacket copy = ClaimDevelopmentLeanPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public Claim getNetClaim(Claim cededClaim) {
        ClaimDevelopmentLeanPacket netClaim = (ClaimDevelopmentLeanPacket) copy();
        netClaim.setIncurred(netClaim.getIncurred() - ((ClaimDevelopmentLeanPacket) cededClaim).getIncurred());
        netClaim.paid -= ((ClaimDevelopmentLeanPacket) cededClaim).paid;
        return netClaim;
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public void plus(Claim claim) {
        setIncurred(getIncurred() + ((ClaimDevelopmentLeanPacket) claim).getIncurred());
        paid += ((ClaimDevelopmentLeanPacket) claim).getPaid();
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public void minus(Claim claim) {
        setIncurred(getIncurred() - ((ClaimDevelopmentLeanPacket) claim).getIncurred());
        paid -= ((ClaimDevelopmentLeanPacket) claim).getPaid();
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);
        paid *= factor;
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

    public void set(ClaimDevelopmentLeanPacket claim) {
        setOrigin(claim.getOrigin());
        setUltimate(claim.getUltimate());
        setPaid(claim.getPaid());
        setOriginalClaim(claim.getOriginalClaim());
        setEvent(claim.getEvent());
        setFractionOfPeriod(claim.getFractionOfPeriod());
        setClaimType(claim.getClaimType());
        setPeril(claim.getPeril());
        setLineOfBusiness(getLineOfBusiness());
        setReinsuranceContract(claim.getReinsuranceContract());
    }

    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>(3);
        valuesToSave.put(INCURRED, getUltimate());
        valuesToSave.put(PAID, paid);
        valuesToSave.put(RESERVED, getUltimate() - paid);
        return valuesToSave;
    }

    @Override
    public List<String> getFieldNames() {
        return Arrays.asList(INCURRED, PAID, RESERVED);
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
        return getUltimate() - paid;
    }

    public void setReserved(double reserved) {
        this.reserved = reserved;
    }
}
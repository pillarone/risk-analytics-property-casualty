package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;

import java.util.Map;
import java.util.Arrays;
import java.util.List;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class ClaimDevelopmentWithIBNRPacket extends ClaimDevelopmentPacket {

    private double reported;
    private double ibnr;

    private Pattern reportedPattern;

    public ClaimDevelopmentWithIBNRPacket(){
    }

    public ClaimDevelopmentWithIBNRPacket(Claim claim){
        set(claim);
    }

    @Override
    public Claim copy() {
        ClaimDevelopmentWithIBNRPacket copy = ClaimDevelopmentWithIBNRPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public Claim getNetClaim(Claim cededClaim) {
        ClaimDevelopmentWithIBNRPacket netClaim = (ClaimDevelopmentWithIBNRPacket) copy();
        netClaim.minus(cededClaim);
        return netClaim;
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the object received as argument are of equal type.
     */
    @Override
    public void plus(Claim claim) {
        super.plus(claim);
        setReported(reported + ((ClaimDevelopmentWithIBNRPacket) claim).getReported());
        setReported(ibnr + ((ClaimDevelopmentWithIBNRPacket) claim).getIbnr());
    }

    /**
     *  This function is not secure in the sense that it does not check if the object itself and
     *  the obejct received as argument are of equal type.
     */
    @Override
    public void minus(Claim claim) {
        super.plus(claim);
        setReported(reported + ((ClaimDevelopmentWithIBNRPacket) claim).getReported());
        setReported(ibnr + ((ClaimDevelopmentWithIBNRPacket) claim).getIbnr());
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);
        reported *= factor;
        ibnr *= factor;
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

    public void set(ClaimDevelopmentWithIBNRPacket claim) {
        super.set(claim);
        setReported(claim.getReported());
        setIbnr(claim.getIbnr());
    }

    private static final String REPORTED = "reported";
    private static final String IBNR = "IBNR";

    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = super.getValuesToSave();
        valuesToSave.put(REPORTED, reported);
        valuesToSave.put(IBNR, ibnr);
        return valuesToSave;
    }

    @Override
    public List<String> getFieldNames() {
        return Arrays.asList(INCURRED, PAID, REPORTED, RESERVED, IBNR, CHANGE_IN_RESERVES);
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(getIncurred());
        result.append(separator);
        result.append(getPaid());
        result.append(separator);
        result.append(getReserved());
        result.append(separator);
        result.append(reported);
        result.append(separator);
        result.append(ibnr);
        result.append(separator);
        if (getClaimType() != null) result.append(getClaimType()).append(separator);
        if (origin != null) result.append(origin.getName()).append(separator);
        if (getOriginalClaim() != null) result.append(System.identityHashCode(getOriginalClaim())).append(separator);
        if (getLineOfBusiness() != null) result.append(getLineOfBusiness().getName()).append(separator);
        if (getPeril() != null) result.append(getPeril().getName()).append(separator);
        if (getReinsuranceContract() != null) result.append(getReinsuranceContract().getName()).append(separator);
        return result.toString();
    }

    public double getReported() {
        return reported;
    }

    public void setReported(double reported) {
        this.reported = reported;
    }

    public double getIbnr() {
        return ibnr;
    }

    public void setIbnr(double ibnr) {
        this.ibnr = ibnr;
    }

    public Pattern getReportedPattern() {
        return reportedPattern;
    }

    public void setReportedPattern(Pattern reportedPattern) {
        this.reportedPattern = reportedPattern;
    }
}
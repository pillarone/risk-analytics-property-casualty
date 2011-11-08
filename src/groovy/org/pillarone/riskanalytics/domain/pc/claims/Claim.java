package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareAndRetention;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo;
import org.pillarone.riskanalytics.domain.utils.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic claim object not recommended for development and inflation
 */
public class Claim extends MultiValuePacket {

    private double ultimate;
    private Claim originalClaim;
    private Event event;
    @Deprecated
    private Double fractionOfPeriod = 0d;
    private ClaimType claimType;
    private UnderwritingInfo exposure;

    private static final String ULTIMATE = "ultimate";

    public Claim() {
    }

    public Claim copy() {
        Claim copy = ClaimPacketFactory.createPacket();
        copy.set(this);
        return copy;
    }

    @Override
    public void initDefaultPacket() {
        this.originalClaim=this;
    }

    public void set(Claim claim) {
        setOrigin(claim.getOrigin());
        setUltimate(claim.getUltimate());
        setOriginalClaim(claim.getOriginalClaim());
        setEvent(claim.getEvent());
        setFractionOfPeriod(claim.getFractionOfPeriod());
        setDate(claim.getDate());
        setClaimType(claim.getClaimType());
        addMarker(IPerilMarker.class, claim.getPeril());
        addMarker(ISegmentMarker.class, claim.getLineOfBusiness());
        addMarker(IReinsuranceContractMarker.class, claim.getReinsuranceContract());
        setExposure(claim.getExposure());
    }

    public void plus(Claim claim) {
        ultimate += claim.getUltimate();
    }

    public void minus(Claim claim) {
        ultimate -= claim.getUltimate();
    }

    public Claim scale(double factor) {
        ultimate *= factor;
        return this;
    }

    public Claim getNetClaim(Claim cededClaim) {
        Claim netClaim = copy();
        netClaim.ultimate -= cededClaim.ultimate;
        if (cededClaim.notNull()) {
            netClaim.addMarker(IReinsuranceContractMarker.class, cededClaim.getReinsuranceContract());
        }
        if (hasExposureInfo()) {
            double coverRatio = netClaim.getUltimate() / getUltimate();
            netClaim.setExposure(exposure.copy().scale(coverRatio));
        }
        return netClaim;
    }

    public void updateExposureWithFac(FacShareAndRetention facShareAndRetention) {
        double facQuotaShare = facShareAndRetention.getQuotaShare(exposure);
        double facSurplus = facShareAndRetention.getSurplusShare(exposure);
        exposure = exposure.copy();
        exposure.setFacQuotaShare(facQuotaShare);
        exposure.setFacSurplus(facSurplus);
    }

    public double getFacShare(IReinsuranceContractStrategy contractStrategy) {
        if (exposure != null) {
            return exposure.getFacQuotaShare();
        }
        else {
            return 0d;
        }
    }

    public boolean notNull() {
        return ultimate != 0;
    }

    /**
     * To be called after fractionOfPeriod is set!
     * @param periodScope
     */
    public void setDate(PeriodScope periodScope) {
        if (periodScope != null && periodScope.getPeriodCounter() != null) {
            setDate(DateTimeUtilities.getDate(periodScope, fractionOfPeriod));
        }
    }

    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>(1);
        valuesToSave.put(ULTIMATE, ultimate);
        return valuesToSave;
    }

    @Override
    public List<String> getFieldNames() {
        return Arrays.asList(ULTIMATE);
    }

    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(ultimate);
        result.append(separator);
        if (claimType != null) result.append(claimType).append(separator);
        if (origin != null) result.append(origin.getName()).append(separator);
        if (originalClaim != null) result.append(System.identityHashCode(originalClaim)).append(separator);
        if (getLineOfBusiness() != null) result.append(getLineOfBusiness().getName()).append(separator);
        if (getPeril() != null) result.append(getPeril().getName()).append(separator);
        if (getReinsuranceContract() != null) result.append(getReinsuranceContract().getName()).append(separator);
        return result.toString();
    }

    public Double getFractionOfPeriod() {
        return fractionOfPeriod;
    }

    public void setFractionOfPeriod(Double fractionOfPeriod) {
        this.fractionOfPeriod = fractionOfPeriod;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public void setClaimType(ClaimType claimType) {
        this.claimType = claimType;
    }

    public Claim getOriginalClaim() {
        return originalClaim;
    }

    public void setOriginalClaim(Claim originalClaim) {
        this.originalClaim = originalClaim;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public IComponentMarker getPeril() {
        IComponentMarker marker = getMarkedSender(IPerilMarker.class);
        if (marker == null) {
            marker = getMarkedSender(IReserveMarker.class);
        }
        return marker;
    }

    public ISegmentMarker getLineOfBusiness() {
        return (ISegmentMarker) getMarkedSender(ISegmentMarker.class);
    }

    public double getUltimate() {
        return ultimate;
    }

    public void setUltimate(double ultimate) {
        this.ultimate = ultimate;
    }

    /**
     * @deprecated use getIncurred()
     * @return
     */
    @Deprecated
    public double getValue() {
        return ultimate;
    }

    /**
     * @deprecated use setIncurred()
     * @param ultimate
     */
    @Deprecated
    public void setValue(double ultimate) {
        this.ultimate = ultimate;
    }

    public IReinsuranceContractMarker getReinsuranceContract() {
        return (IReinsuranceContractMarker) getMarkedSender(IReinsuranceContractMarker.class);
    }

    public UnderwritingInfo getExposure() {
        return exposure;
    }

    public void setExposure(UnderwritingInfo exposure) {
        this.exposure = exposure;
    }

    public  boolean hasExposureInfo() {
        return this.exposure != null;
    }
}

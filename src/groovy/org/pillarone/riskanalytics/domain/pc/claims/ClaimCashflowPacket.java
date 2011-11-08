package org.pillarone.riskanalytics.domain.pc.claims;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimCashflowPacket extends MultiValuePacket {

    private double paid;
    private double reported;
    private double ibnr;

    /** the updateDate might be the incurred, paid, reported date, depending on the updated double properties above */
    private DateTime updateDate;

    private final ClaimRoot originalClaim;

    private IPerilMarker peril;
    private ISegmentMarker lineOfBusiness;
    private IReinsuranceContractMarker reinsuranceContract;
    private ICompanyMarker company;

    public ClaimCashflowPacket(ClaimRoot originalClaim) {
        this.originalClaim = originalClaim;
    }

    public ClaimCashflowPacket(double paid, double reported, double ibnr, ClaimRoot originalClaim, DateTime updateDate) {
        this.paid = paid;
        this.reported = reported;
        this.ibnr = ibnr;
        this.originalClaim = originalClaim;
        this.updateDate = updateDate;
        setDate(updateDate);
    }

    private ClaimCashflowPacket(ClaimCashflowPacket packet, DateTime persistenceDate) {
        throw new NotImplementedException();
    }

    public double reserved() {
        throw new NotImplementedException();
    }

    public double  changeInReserves() {
        throw new NotImplementedException();
    }

    public double  developmentResult() {
        throw new NotImplementedException();
    }

    public int occurrencePeriod(IPeriodCounter periodCounter) {
        throw new NotImplementedException();
    }

    public int updatePeriod(IPeriodCounter periodCounter) {
        throw new NotImplementedException();
    }

    public ClaimCashflowPacket getClaimCashflow() {
        return this;
    }

    /**
     * Helper method to fill underwriting period channels
     * @return
     */
    public ClaimCashflowPacket getClaimUnderwritingPeriod() {
        return new ClaimCashflowPacket(this, originalClaim.getOccurrenceDate());
    }

    /**
     * Utility method to derive cashflow claims of an original claim using its ultimate and pattern
     * @param originalClaim
     * @param developmentPeriod
     * @return
     */
    public static ClaimCashflowPacket getClaimCashflow(ClaimRoot originalClaim, int developmentPeriod) {
        throw new NotImplementedException();
    }

    /**
     * Add 'properties' calculated on the fly
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = super.getValuesToSave();
        valuesToSave.put("reserved", reserved());
        valuesToSave.put("changeInReserves", changeInReserves());
        valuesToSave.put("developmentResult", developmentResult());
        return valuesToSave;
    }

    /**
     * Add 'properties' calculated on the fly
     * @return
     */
    @Override
    public List<String> getFieldNames() {
        List<String> fieldNames = super.getFieldNames();
        fieldNames.add("reserved");
        fieldNames.add("changeInReserves");
        fieldNames.add("developmentResult");
        return fieldNames;
    }
}

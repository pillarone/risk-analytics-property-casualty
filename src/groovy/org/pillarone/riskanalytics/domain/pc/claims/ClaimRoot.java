package org.pillarone.riskanalytics.domain.pc.claims;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.Pattern;
import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo;

/**
 * Doc: PMO-1540
 * The ClaimRoot replaces the current originalClaim of a Claim object. It contains all shared information of several
 * ClaimCashflowPacket objects and is used as a key.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public final class ClaimRoot {

    private double nominalUltimate;
    private Event event;
    private ClaimType claimType;
    private ExposureInfo exposureInfo;
    private DateTime exposureStartDate;
    private DateTime occurrenceDate;
    private Pattern payoutPattern;
    private Pattern reportingPattern;


    public ClaimRoot(double nominalUltimate, Event event, ClaimType claimType, ExposureInfo exposureInfo,
                     DateTime occurrenceDate, Pattern payoutPattern, Pattern reportingPattern) {
        this.nominalUltimate = nominalUltimate;
        this.event = event;
        this.claimType = claimType;
        this.exposureInfo = exposureInfo;
        exposureStartDate = exposureInfo.getDate();
        this.occurrenceDate = occurrenceDate;
        this.payoutPattern = payoutPattern;
        this.reportingPattern = reportingPattern;
    }

    public ClaimRoot(double nominalUltimate, Event event, ClaimType claimType, DateTime exposureStartDate,
                     DateTime occurrenceDate, Pattern payoutPattern, Pattern reportingPattern) {
        this.nominalUltimate = nominalUltimate;
        this.event = event;
        this.claimType = claimType;
        this.exposureStartDate = exposureStartDate;
        this.occurrenceDate = occurrenceDate;
        this.payoutPattern = payoutPattern;
        this.reportingPattern = reportingPattern;
    }

    public double getNominalUltimate() {
        return nominalUltimate;
    }

    public Event getEvent() {
        return event;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public ExposureInfo getExposureInfo() {
        return exposureInfo;
    }

    public DateTime getExposureStartDate() {
        return exposureStartDate;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public Pattern getPayoutPattern() {
        return payoutPattern;
    }

    public Pattern getReportingPattern() {
        return reportingPattern;
    }
}

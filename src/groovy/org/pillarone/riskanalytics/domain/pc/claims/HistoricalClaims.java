package org.pillarone.riskanalytics.domain.pc.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;
import org.pillarone.riskanalytics.domain.utils.DateTimeUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Filters claims packets by (& allocates each claim to a) period.
 * Sends all historical claims packets to following components at simulation start (period=0).
 * An historical claim is one whose occurrence (loss event date) lies before the simulation start date.
 * Claims packets occurring after the start of (historical) simulation (i.e. with period>0) are dropped, by design.
 * The claim occurrence date is when the loss event incurred.
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class HistoricalClaims extends Component implements PerilMarker {

    private static Log LOG = LogFactory.getLog(HistoricalClaims.class);

    /** Enables the component to access the current iteration and period number. Instance injected by framework. */
    private IterationScope iterationScope;

    /** Intermediary store for Claim packets up to the simulation start date. */
    private List<Claim> claims;

    /** Provides Claim packets (a) at simulation start period, or (b) of current period */
    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);

    /** Condensed historical claims data */
    private TableMultiDimensionalParameter parmEventLosses = new TableMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{"2010-01-01", "2010-01-15", "0d", "0d"}),
            Arrays.asList("Date Incurred", "Date Paid", "Amount Incurred", "Amount Paid"));

    private static final int DATE_INCURRED = 0;
    private static final int DATE_PAID = 1;
    private static final int AMOUNT_INCURRED = 2;
    private static final int AMOUNT_PAID = 3;

    @Override
    protected void doCalculation() {
        boolean isFirstIteration = iterationScope.isFirstIteration();
        boolean isFirstPeriod = iterationScope.getPeriodScope().isFirstPeriod();
        if (isFirstIteration && isFirstPeriod) {
            initSimulation();
        }
        if (claims != null && isFirstPeriod) {
            outClaims.addAll(claims);
        }
    }

    protected void initSimulation() {
        DateTime startDate = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        int numberOfClaims = parmEventLosses.getValueRowCount();
        claims = new ArrayList<Claim>(numberOfClaims);
        for (int row = 1; row <= numberOfClaims; row++) {
            DateTime incurredDate = DateTimeUtilities.convertToDateTime((String) parmEventLosses.getValueAt(row, DATE_INCURRED));
            DateTime paidDate = DateTimeUtilities.convertToDateTime((String) parmEventLosses.getValueAt(row, DATE_PAID));
            // assert paidDate.isAfter(incurredDate)
            // filter: ignore claims not incurred and paid strictly before the simulation start date
            if (paidDate.isBefore(startDate)) {
                Event event = new Event();
                double fractionOfPeriod = DateTimeUtilities.mapDateToFractionOfPeriod(incurredDate);
                event.setFractionOfPeriod(fractionOfPeriod);
                ClaimDevelopmentPacket claim = new ClaimDevelopmentPacket();
                claim.setEvent(event);
                claim.setIncurred((Double) parmEventLosses.getValueAt(row, AMOUNT_INCURRED));
                claim.setPaid((Double) parmEventLosses.getValueAt(row, AMOUNT_PAID));
                claim.setOriginalPeriod(DateTimeUtilities.mapDateToPeriod(incurredDate, startDate));
                claim.setFractionOfPeriod(fractionOfPeriod);
                // timeslot allocation: group all claims into a single list
                claims.add(claim);
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("row " + row + " contains a future claim.");
                }
            }
        }
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public TableMultiDimensionalParameter getParmEventLosses() {
        return parmEventLosses;
    }

    public void setParmEventLosses(TableMultiDimensionalParameter parmEventLosses) {
        this.parmEventLosses = parmEventLosses;
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }
}
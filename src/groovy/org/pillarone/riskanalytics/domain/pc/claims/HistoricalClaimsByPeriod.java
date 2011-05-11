package org.pillarone.riskanalytics.domain.pc.claims;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event;
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket;
import org.pillarone.riskanalytics.domain.utils.DateTimeUtilities;

import java.util.*;

/**
 * Filters claims packets by (& allocates each claim to a) period.
 * Sends all historical claims packets to following components for each period<=0, i.e. up to simulation start.
 * An historical claim is one whose occurrence (loss event date) lies before the simulation start date.
 * Claims packets occurring after the start of (historical) simulation (i.e. with period>0) are dropped, by design.
 * The claim occurrence date is when the loss event incurred.
 *
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
@ComponentCategory(categories = {"CLAIM","FILTER"})
public class HistoricalClaimsByPeriod extends Component implements PerilMarker {

    private static Log LOG = LogFactory.getLog(HistoricalClaimsByPeriod.class);

    /** Enables the component to access the current iteration and period number. Instance injected by framework. */
    private IterationScope iterationScope;

    /** Intermediary store for Claim packets for each historical period(<=0, i.e. up to simulation start). */
    private Map<Integer, List<Claim>> claimsByPeriod = new HashMap<Integer, List<Claim>>();

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
        int currentIteration = iterationScope.getCurrentIteration();
        int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
        if (iterationScope.isFirstIteration() && iterationScope.getPeriodScope().isFirstPeriod()) {
            initSimulation();
        }
        List<Claim> claims = claimsByPeriod.get(currentPeriod);
        if (claims != null) {
            outClaims.addAll(claims);
        }
    }

    protected void initSimulation() {
        DateTime startDate = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        int numberOfClaims = parmEventLosses.getValueRowCount();
        for (int row = 1; row <= numberOfClaims; row++) {
            DateTime incurredDate = DateTimeUtilities.convertToDateTime((String) parmEventLosses.getValueAt(row, DATE_INCURRED));
            int period = DateTimeUtilities.mapDateToPeriod(incurredDate, startDate);
            if (period <= 0) {
                // filter: ignore claims after simulation start date
                Event event = new Event();
                event.setFractionOfPeriod(DateTimeUtilities.mapDateToFractionOfPeriod(incurredDate));
                ClaimDevelopmentPacket claim = new ClaimDevelopmentPacket();
                claim.setOriginalPeriod(period);
                claim.setEvent(event);
                claim.setIncurred((Double) parmEventLosses.getValueAt(row, AMOUNT_INCURRED));
                claim.setPaid((Double) parmEventLosses.getValueAt(row, AMOUNT_PAID));
                // timeslot allocation: group claims by period
                List<Claim> claimsOfPeriod = claimsByPeriod.get(period);
                if (claimsOfPeriod != null) {
                    claimsOfPeriod.add(claim);
                }
                else {
                    claimsOfPeriod = new ArrayList<Claim>();
                    claimsOfPeriod.add(claim);
                    claimsByPeriod.put(period, claimsOfPeriod);
                }
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
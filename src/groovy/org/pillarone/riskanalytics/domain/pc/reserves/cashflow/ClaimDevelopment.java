package org.pillarone.riskanalytics.domain.pc.reserves.cashflow;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker;
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): parameter validation such as ie pattern length and historic claims
public class ClaimDevelopment extends Component implements IReserveMarker {

    private PeriodStore periodStore;
    private SimulationScope simulationScope;
    private PeriodScope periodScope;
    private boolean developmentWithIBNR = false;

    private static final String RESERVES = "reserves";

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);

    private PacketList<Claim> outClaims = new PacketList<Claim>(Claim.class);
    private PacketList<ClaimDevelopmentPacket> outClaimsDevelopment = new PacketList<ClaimDevelopmentPacket>(ClaimDevelopmentPacket.class);                             // todo(sku): remove as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentWithIBNRPacket> outClaimsDevelopmentWithIBNR = new PacketList<ClaimDevelopmentWithIBNRPacket>(ClaimDevelopmentWithIBNRPacket.class);     // todo(sku): remove as soon as PMO-648 is resolved

    private ComboBoxTableMultiDimensionalParameter parmAppliedOnPerils = new ComboBoxTableMultiDimensionalParameter(
        Arrays.asList(""), Arrays.asList("peril"), PerilMarker.class);
    private IPatternStrategy parmPayoutPattern = PatternStrategyType.getStrategy(PatternStrategyType.NONE, Collections.emptyMap());
    private IPatternStrategy parmReportedPattern = PatternStrategyType.getStrategy(PatternStrategyType.NONE, Collections.emptyMap());
    private IHistoricClaimsStrategy parmActualClaims = HistoricClaimsStrategyType.getStrategy(HistoricClaimsStrategyType.NONE, Collections.emptyMap());


    protected void doCalculation() {
        int currentPeriod = periodScope.getCurrentPeriod();
        Pattern payoutPattern = new Pattern(parmPayoutPattern);
        Pattern reportedPattern = new Pattern(parmReportedPattern);
        if (periodScope.isFirstPeriod()) {
            developmentWithIBNR = !reportedPattern.isTrivial();
            processHistoricClaims(payoutPattern, reportedPattern);
        }
        applyPatternOnNewClaimsAndFillPeriodStore(filteredClaims(), payoutPattern, reportedPattern, currentPeriod);
        List<Claim> currentPeriodClaims = (List<Claim>) periodStore.get(RESERVES, 0);
        if (currentPeriodClaims != null) {
            outClaims.addAll(currentPeriodClaims);
            if (!developmentWithIBNR) {
                for (Claim claim : currentPeriodClaims) {
                    outClaimsDevelopment.add((ClaimDevelopmentPacket) claim);
                }
            }
            else {
                for (Claim claim : currentPeriodClaims) {
                    outClaimsDevelopmentWithIBNR.add((ClaimDevelopmentWithIBNRPacket) claim);
                }
            }
        }
    }

    private void processHistoricClaims(Pattern payoutPattern, Pattern reportedPattern) {
        if (parmActualClaims.getType().equals(HistoricClaimsStrategyType.NONE)) {
            return;
        }
        if (parmActualClaims.getType().equals(HistoricClaimsStrategyType.LAST_REPORTED)) {
            processHistoricLastReportedClaims(payoutPattern, reportedPattern);
        }
        else if (parmActualClaims.getType().equals(HistoricClaimsStrategyType.LAST_PAID)) {
            processHistoricLastPaidClaims(payoutPattern, reportedPattern);
        }
    }

    private void processHistoricLastPaidClaims(Pattern payoutPattern, Pattern reportedPattern) {
        if (!reportedPattern.isTrivial()) {
            for (Map.Entry<Integer, Double> entry : parmActualClaims.getDiagonalValues().entrySet()) {
                int developmentPeriod = entry.getKey();
                double incrementalPaid = entry.getValue();
                double incurredClaim = 0;
                if (!developmentWithIBNR) {
                    incurredClaim = incrementalPaid / payoutPattern.incrementFactor(developmentPeriod);
                }
                else {
                    if (developmentPeriod == 1) {
                        incurredClaim = incrementalPaid / paidReportedPatternProduct(payoutPattern, reportedPattern, developmentPeriod-1);
                    }
                    else {
                        incurredClaim = incrementalPaid / differencePaidReportedPatternProduct(payoutPattern, reportedPattern, developmentPeriod-1);
                    }
                }
                applyPatternOnReservesAndFillPeriodStore(incurredClaim, payoutPattern, reportedPattern, developmentPeriod);
            }
        }
    }

    private void processHistoricLastReportedClaims(Pattern payoutPattern, Pattern reportedPattern) {
        if (!developmentWithIBNR) {
            throw new IllegalArgumentException("ClaimDevelopment.mismatchHistoricClaimsAndPatterns");
        }
        for (Map.Entry<Integer, Double> entry : parmActualClaims.getDiagonalValues().entrySet()) {
            int developmentPeriod = entry.getKey();
            double reported = entry.getValue();
            double incurredClaim = reported / reportedPattern.cumulativeFactor(developmentPeriod-1);
            applyPatternOnReservesAndFillPeriodStore(incurredClaim, payoutPattern, reportedPattern, developmentPeriod);
        }
    }

    private double differencePaidReportedPatternProduct(Pattern payoutPattern, Pattern reportedPattern, int developmentPeriod) {
        return paidReportedPatternProduct(payoutPattern, reportedPattern, developmentPeriod)
                                      - (paidReportedPatternProduct(payoutPattern, reportedPattern, developmentPeriod-1));
    }

    private double paidReportedPatternProduct(Pattern payoutPattern, Pattern reportedPattern, int developmentPeriod) {
        return payoutPattern.cumulativeFactor(developmentPeriod) * reportedPattern.cumulativeFactor(developmentPeriod);
    }

    private void applyPatternOnReservesAndFillPeriodStore(double incurredClaim, Pattern payoutPattern, Pattern reportedPattern, int developmentPeriod) {
        Claim claim = ClaimPacketFactory.createPacket();
        claim.setUltimate(incurredClaim);
        claim.setClaimType(ClaimType.AGGREGATED_RESERVES);
        if (developmentWithIBNR) {
            fillPeriodStore(applyPatternOnReserves(claim, payoutPattern, reportedPattern, -developmentPeriod));
        }
        else {
            fillPeriodStore(applyPatternOnReserves(claim, payoutPattern, -developmentPeriod));
        }
    }

    private List<ClaimDevelopmentWithIBNRPacket> applyPatternOnReserves(Claim claim, Pattern payoutPattern, Pattern reportedPattern, int currentPeriod) {
        List<ClaimDevelopmentWithIBNRPacket> claims = new ArrayList<ClaimDevelopmentWithIBNRPacket>();
        double ultimate = claim.getUltimate();
        for (int developmentPeriod = -currentPeriod; developmentPeriod < payoutPattern.size(); developmentPeriod++) {
            ClaimDevelopmentWithIBNRPacket claimDevelopment = ClaimDevelopmentWithIBNRPacketFactory.createPacket();
            claimDevelopment.set(claim);
            claimDevelopment.setOrigin(this);
            claimDevelopment.setOriginalPeriod(currentPeriod);
            claimDevelopment.setUltimate(0);
            claimDevelopment.setPaid(ultimate * differencePaidReportedPatternProduct(payoutPattern, reportedPattern, developmentPeriod));
            double reported = ultimate * reportedPattern.cumulativeFactor(developmentPeriod);
            claimDevelopment.setReported(reported);
            claimDevelopment.setIbnr(ultimate - reported);
            claims.add(claimDevelopment);
        }
        return claims;
    }

    private List<ClaimDevelopmentPacket> applyPatternOnReserves(Claim claim, Pattern payoutPattern, int currentPeriod) {
        List<ClaimDevelopmentPacket> claims = new ArrayList<ClaimDevelopmentPacket>();
        double ultimate = claim.getUltimate();
        for (int developmentPeriod = -currentPeriod; developmentPeriod < payoutPattern.size(); developmentPeriod++) {
            ClaimDevelopmentPacket claimDevelopment = ClaimDevelopmentPacketFactory.createPacket();
            claimDevelopment.set(claim);
            claimDevelopment.setOrigin(this);
            claimDevelopment.setOriginalPeriod(currentPeriod);
            claimDevelopment.setUltimate(0);
            claimDevelopment.setPaid(ultimate * payoutPattern.incrementFactor(developmentPeriod));
            claims.add(claimDevelopment);
        }
        return claims;
    }

    private void applyPatternOnNewClaimsAndFillPeriodStore(List<Claim> claims, Pattern payoutPattern, Pattern reportedPattern, int currentPeriod) {
        if (developmentWithIBNR) {
            for (Claim claim : claims) {
                fillPeriodStore(applyPatternOnNewClaims(claim, payoutPattern, reportedPattern, currentPeriod));
            }
        }
        else {
            for (Claim claim : claims) {
                fillPeriodStore(applyPatternOnNewClaims(claim, payoutPattern, currentPeriod));
            }
        }
    }

    private List<ClaimDevelopmentWithIBNRPacket> applyPatternOnNewClaims(Claim claim, Pattern payoutPattern, Pattern reportedPattern, int currentPeriod) {
        List<ClaimDevelopmentWithIBNRPacket> claims = new ArrayList<ClaimDevelopmentWithIBNRPacket>(payoutPattern.size());
        if (payoutPattern.size() == 0 || payoutPattern.getCumulativeValues().get(0) == 1) {
            ClaimDevelopmentWithIBNRPacket claimDeveloped = ClaimDevelopmentWithIBNRPacketFactory.createPacket();
            claimDeveloped.set(claim);
            claimDeveloped.setOrigin(this);
            claimDeveloped.setOriginalPeriod(currentPeriod);
            claimDeveloped.setPaid(claim.getUltimate());
            claimDeveloped.setChangeInReserves(claim.getUltimate());
            claims.add(claimDeveloped);
        }
        else {
            // todo(sku): handle patterns of different length
            double formerReported = 0d;
            double formerCaseReserves = 0d;
            double formerIBNR = 0d;
            for (int developmentPeriod = 0; developmentPeriod < payoutPattern.size(); developmentPeriod++) {
                ClaimDevelopmentWithIBNRPacket claimDeveloped = ClaimDevelopmentWithIBNRPacketFactory.createPacket();
                claimDeveloped.set(claim);
                claimDeveloped.setOrigin(this);
                claimDeveloped.setOriginalPeriod(currentPeriod);
                if (developmentPeriod > 0 || currentPeriod < 0) claimDeveloped.setUltimate(0d);
                double reported = claim.getUltimate() * reportedPattern.cumulativeFactor(developmentPeriod);
                double ibnr = claim.getUltimate() - reported;
                double paid = 0;
                double caseReserves = 0;
                double changeInTotalReserves = 0d;
                if (developmentPeriod == 0) {
                    paid = reported * payoutPattern.incrementFactor(developmentPeriod);
                    caseReserves = reported - paid;
                    changeInTotalReserves = -paid;
                }
                else {
                    paid = formerReported * payoutPattern.incrementFactor(developmentPeriod)
                            + (reported - formerReported) * payoutPattern.cumulativeFactor(developmentPeriod);
                    caseReserves = reported - paid + formerCaseReserves - formerReported;
                    changeInTotalReserves = ibnr - formerIBNR + caseReserves - formerCaseReserves;
                }
                claimDeveloped.setReported(reported);
                claimDeveloped.setPaid(paid);
                claimDeveloped.setReserved(caseReserves);
                claimDeveloped.setIbnr(ibnr);
                claimDeveloped.setChangeInReserves(changeInTotalReserves);
                formerReported = reported;
                formerCaseReserves = caseReserves;
                formerIBNR = ibnr;
                claims.add(claimDeveloped);
            }
        }
        return claims;
    }

    private List<ClaimDevelopmentPacket> applyPatternOnNewClaims(Claim claim, Pattern payoutPattern, int currentPeriod) {
        List<ClaimDevelopmentPacket> claims = new ArrayList<ClaimDevelopmentPacket>(payoutPattern.size());
        if (payoutPattern.size() == 0 || payoutPattern.getCumulativeValues().get(0) == 1) {
            ClaimDevelopmentPacket claimDeveloped = ClaimDevelopmentPacketFactory.createPacket();
            claimDeveloped.set(claim);
            claimDeveloped.setPaid(claim.getUltimate());
            claimDeveloped.setChangeInReserves(claim.getUltimate());
            claimDeveloped.setOriginalPeriod(currentPeriod);
            claims.add(claimDeveloped);
        }
        else {
            double formerReserves = claim.getUltimate();
            for (int developmentPeriod = 0; developmentPeriod < payoutPattern.size(); developmentPeriod++) {
                ClaimDevelopmentPacket claimDeveloped = ClaimDevelopmentPacketFactory.createPacket();
                claimDeveloped.set(claim);
                if (developmentPeriod > 0) claimDeveloped.setUltimate(0d);
                double paid = claim.getUltimate() * payoutPattern.incrementFactor(developmentPeriod);
                claimDeveloped.setPaid(paid);
                double reserves = formerReserves - paid;
                claimDeveloped.setReserved(reserves);
                claimDeveloped.setChangeInReserves(paid);
                claimDeveloped.setPayoutPattern(payoutPattern);
                claimDeveloped.setOriginalPeriod(currentPeriod);
                claims.add(claimDeveloped);
                formerReserves = reserves;
            }
        }
        return claims;
    }

    private void fillPeriodStore(List<? extends Claim> claims) {
        for (int period = 0; period < claims.size(); period++) {
            List claimsOfFuturePeriod = (List) periodStore.get(RESERVES, period);
            if (claimsOfFuturePeriod == null) {
                claimsOfFuturePeriod = new ArrayList();
                periodStore.put(RESERVES, claimsOfFuturePeriod, period);
            }
            claimsOfFuturePeriod.add(claims.get(period));
        }
    }

    /**
     * @return claims produced by covered perils
     */
    private List<Claim> filteredClaims() {
        List<PerilMarker> coveredPerils = parmAppliedOnPerils.getValuesAsObjects();
        return ClaimFilterUtilities.filterClaimsByPeril(inClaims, coveredPerils);
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<Claim> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<Claim> outClaims) {
        this.outClaims = outClaims;
    }

    public PacketList<ClaimDevelopmentPacket> getOutClaimsDevelopment() {
        return outClaimsDevelopment;
    }

    public void setOutClaimsDevelopment(PacketList<ClaimDevelopmentPacket> outClaimsDevelopment) {
        this.outClaimsDevelopment = outClaimsDevelopment;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public IPatternStrategy getParmPayoutPattern() {
        return parmPayoutPattern;
    }

    public void setParmPayoutPattern(IPatternStrategy parmPayoutPattern) {
        this.parmPayoutPattern = parmPayoutPattern;
    }

    public IPatternStrategy getParmReportedPattern() {
        return parmReportedPattern;
    }

    public void setParmReportedPattern(IPatternStrategy parmReportedPattern) {
        this.parmReportedPattern = parmReportedPattern;
    }

    public PacketList<ClaimDevelopmentWithIBNRPacket> getOutClaimsDevelopmentWithIBNR() {
        return outClaimsDevelopmentWithIBNR;
    }

    public void setOutClaimsDevelopmentWithIBNR(PacketList<ClaimDevelopmentWithIBNRPacket> outClaimsDevelopmentWithIBNR) {
        this.outClaimsDevelopmentWithIBNR = outClaimsDevelopmentWithIBNR;
    }

    public IHistoricClaimsStrategy getParmActualClaims() {
        return parmActualClaims;
    }

    public void setParmActualClaims(IHistoricClaimsStrategy parmActualClaims) {
        this.parmActualClaims = parmActualClaims;
    }

    public ComboBoxTableMultiDimensionalParameter getParmAppliedOnPerils() {
        return parmAppliedOnPerils;
    }

    public void setParmAppliedOnPerils(ComboBoxTableMultiDimensionalParameter parmAppliedOnPerils) {
        this.parmAppliedOnPerils = parmAppliedOnPerils;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }
}
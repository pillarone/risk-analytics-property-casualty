package org.pillarone.riskanalytics.domain.pc.reserves.fasttrack;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.ComponentCategory;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.generators.GeneratorCachingComponent;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * <ul class="alternate" type="square">
 * <li>Initial reserves consists of a single value. It is split into a paid and reserved part according
 * to a modifiable (censor, truncate) distribution.</li>
 * <li>"Initial" reserves for periods after the first projected period consist of the sum of the reserved
 * part of the initial reserves of the former period and the newly generated reserved claims.</li>
 * </ul>
 *
 * @author shartmann (at) munichre (dot) com
 */
@ComponentCategory(categories = {"RESERVES","GENERATOR"})
public class ReservesGeneratorLean extends GeneratorCachingComponent implements IReserveMarker {

    private PeriodScope periodScope;

    private PeriodStore periodStore;
    private static final String RESERVES = "reserves";

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);

    private PacketList<Claim> outClaimsDevelopment = new PacketList<Claim>(Claim.class);
    // todo(sku): remove as soon as PMO-648 is resolved
    private PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment = new PacketList<ClaimDevelopmentLeanPacket>(ClaimDevelopmentLeanPacket.class);
    private PacketList<SingleValuePacket> outInitialReserves = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    private double parmInitialReserves = 0d;
    private IReservesGeneratorStrategy parmReservesModel = ReservesGeneratorStrategyType.getStrategy(
            ReservesGeneratorStrategyType.INITIAL_RESERVES,
            ArrayUtils.toMap(new Object[][]{{"basedOnClaimsGenerators", new ComboBoxTableMultiDimensionalParameter(
                    Collections.emptyList(),
                    Arrays.asList("Claims Generators"), IPerilMarker.class)}}));
    private RandomDistribution parmDistribution = DistributionType.getStrategy(DistributionType.CONSTANT,
            ArrayUtils.toMap(new Object[][]{{"constant", 0d}}));
    private DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());
    private double parmPeriodPaymentPortion = 0d;

    protected void doCalculation() {
        ClaimDevelopmentLeanPacket claim = new ClaimDevelopmentLeanPacket();
        claim.setOrigin(this);

        setIncurred(claim);

        claim.setFractionOfPeriod(0.5);

        claim.setPaid(claim.getUltimate() * parmPeriodPaymentPortion);
        claim.setReserved(claim.getUltimate() - claim.getPaid());
        outClaimsDevelopment.add(claim);
        outClaimsLeanDevelopment.add(claim);

        double aggregatedReserves = addReservesFromClaims(claim.getReserved());
        periodStore.put(RESERVES, aggregatedReserves);
    }

    @Override
    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel == inClaims) {
            ComboBoxTableMultiDimensionalParameter basedOnClaimsGenerator = ((AbstractClaimsGeneratorBasedReservesGeneratorStrategy) parmReservesModel).getBasedOnClaimsGenerators();
            List<IPerilMarker> coveredPerils = basedOnClaimsGenerator.getValuesAsObjects(0, true);
            if (coveredPerils.size() == 0) {
                inClaims.addAll(source);
            }
            else {
                for (Object claim : source) {
                    if (coveredPerils.contains(((Claim) claim).getPeril())) {
                        inClaims.add((Claim) claim);
                    }
                }
            }
        }
        else {
            super.filterInChannel(inChannel, source);
        }
    }

    private double addReservesFromClaims(double aggregatedReserves) {
        if (!(parmReservesModel instanceof PriorPeriodReservesGeneratorStrategy)) {
            return aggregatedReserves;
        }
        return addReserves(aggregatedReserves);
    }

    private double addReserves(double aggregatedReserves) {
        for (Claim incomingClaim : inClaims) {
            aggregatedReserves += ((ClaimDevelopmentLeanPacket) incomingClaim).getReserved();
        }
        return aggregatedReserves;
    }

    private void setIncurred(ClaimDevelopmentLeanPacket claimDevelopmentLeanPacket) {

        IRandomNumberGenerator generator = getCachedGenerator(getParmDistribution(), getParmModification());
        Double randomFactor = (Double) generator.nextValue();

        if (parmReservesModel instanceof AbsoluteReservesGeneratorStrategy) {
            claimDevelopmentLeanPacket.setUltimate(randomFactor);
            return;
        }


        if (parmReservesModel instanceof InitialReservesGeneratorStrategy) {
            claimDevelopmentLeanPacket.setUltimate(randomFactor * parmInitialReserves);
            SingleValuePacket initialReserves = new SingleValuePacket();
            initialReserves.setValue(parmInitialReserves);
            initialReserves.setOrigin(this);
            outInitialReserves.add(initialReserves);
            return;
        }

        // parmReservesModel instanceof PriorPeriodReservesGeneratorStrategy = true at this point
        if (periodScope.getCurrentPeriod() == 0) {
            claimDevelopmentLeanPacket.setUltimate(randomFactor * parmInitialReserves);
            SingleValuePacket initialReserves = new SingleValuePacket();
            initialReserves.setValue(parmInitialReserves);
            outInitialReserves.add(initialReserves);
        } else {
            claimDevelopmentLeanPacket.setUltimate(randomFactor * (Double) periodStore.get(RESERVES, -1));
            SingleValuePacket initialReserves = new SingleValuePacket();
            initialReserves.setValue((Double) periodStore.get(RESERVES, -1));
            outInitialReserves.add(initialReserves);
        }
    }

    public PacketList<Claim> getOutClaimsDevelopment() {
        return outClaimsDevelopment;
    }

    public void setOutClaimsDevelopment(PacketList<Claim> outClaimsDevelopment) {
        this.outClaimsDevelopment = outClaimsDevelopment;
    }

    public double getParmPeriodPaymentPortion() {
        return parmPeriodPaymentPortion;
    }

    public void setParmPeriodPaymentPortion(double parmPeriodPaymentPortion) {
        this.parmPeriodPaymentPortion = parmPeriodPaymentPortion;
    }

    public double getParmInitialReserves() {
        return parmInitialReserves;
    }

    public void setParmInitialReserves(double parmInitialReserves) {
        this.parmInitialReserves = parmInitialReserves;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
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

    public IReservesGeneratorStrategy getParmReservesModel() {
        return parmReservesModel;
    }

    public void setParmReservesModel(IReservesGeneratorStrategy parmReservesModel) {
        this.parmReservesModel = parmReservesModel;
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public DistributionModified getParmModification() {
        return parmModification;
    }

    public void setParmModification(DistributionModified parmModification) {
        this.parmModification = parmModification;
    }

    public PacketList<ClaimDevelopmentLeanPacket> getOutClaimsLeanDevelopment() {
        return outClaimsLeanDevelopment;
    }

    public void setOutClaimsLeanDevelopment(PacketList<ClaimDevelopmentLeanPacket> outClaimsLeanDevelopment) {
        this.outClaimsLeanDevelopment = outClaimsLeanDevelopment;
    }

    public PacketList<SingleValuePacket> getOutInitialReserves() {
        return outInitialReserves;
    }

    public void setOutInitialReserves(PacketList<SingleValuePacket> outInitialReserves) {
        this.outInitialReserves = outInitialReserves;
    }
}

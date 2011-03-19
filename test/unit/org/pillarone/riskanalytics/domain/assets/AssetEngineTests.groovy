package org.pillarone.riskanalytics.domain.assets

import models.asset.AssetModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.domain.assets.constants.BondType
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.assets.constants.Seniority
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */

public class AssetEngineTests extends GroovyTestCase {

    void testDoCalculation() {
        DateTime startDate = new DateTime(2008, 11, 14, 0, 0, 0, 0)
        DateTime endDate = new DateTime(2009, 11, 14, 0, 0, 0, 0)

        //PacketList<BondParameters> inBondParameters = new PacketList(BondParameters.class);
        //List<FixedInterestRateBond> fixedInterestRateBonds = (List<FixedInterestRateBond>) periodStore.get(FIXED_INTEREST_RATE_BONDS);
        //AbstractContext simulationContext
        //PeriodStore periodStore
        BondParameters bond = new BondParameters();
        bond.bondType = BondType.CORPORATE_BOND
        bond.setBookValue(1)
        bond.purchasePrice = 1
        bond.quantity = 10
        bond.coupon = 10
        bond.faceValue = 100
        bond.maturityDate = endDate
        bond.installmentPeriod = Duration.standardDays(365); // checking required
        bond.rating = Rating.AAA
        bond.seniority = Seniority.SENIOR_SECURED

        CIRYieldModellingChoices curve = new CIRYieldModellingChoices()

        curve.meanReversionParameter = 0.01
        curve.riskAversionParameter = 0.0
        curve.longRunMean = 0.08
        curve.volatility = 0.1
        curve.initialInterestRate = 0.05

        YieldCurveCIRStrategy yieldCurve = new YieldCurveCIRStrategy(curve)

        CashParameters cash = new CashParameters()

        cash.initialCash = 1000
        cash.minimalCashLevel = 100
        cash.maximalCashLevel = 10000


        FeesParameters fees = new FeesParameters();

        fees.bondDepositFeesPercentageOfMarketValue = 0.0005;
        fees.bondTransactionCostsRate = 0.0005;

        AssetEngine assetEngine = new AssetEngine();
        assetEngine.simulationScope = new SimulationScope(iterationScope: new IterationScope(periodScope: new PeriodScope()), simulation: new Simulation());
        assetEngine.simulationScope.simulation.beginOfFirstPeriod = new DateTime(startDate)
        assetEngine.simulationScope.model = new VoidTestModel()
        assetEngine.periodStore = new PeriodStore(assetEngine.simulationScope.iterationScope.periodScope)
        assetEngine.simulationScope.iterationScope.periodScope.periodCounter = new AssetModel().createPeriodCounter(new DateTime(startDate))
        assetEngine.inBondParameters << bond
        assetEngine.inYieldModellingChoices << curve
        assetEngine.inCashParameters << cash
        assetEngine.inFeesParameters << fees
        FixedInterestRateBond testFIRBond = new FixedInterestRateBond(bond)
        assetEngine.doCalculation()

        assertEquals "Calculation : ", testFIRBond.getMarketValue(startDate, yieldCurve), assetEngine.outBondAccounting.get(0).getMarketValue(), 0.01
    }
}

class VoidTestModel extends StochasticModel {

    public void initComponents() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void wireComponents() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
package models.asset

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.assets.AssetEngine
import org.pillarone.riskanalytics.domain.assets.parameterization.DynamicBonds
import org.pillarone.riskanalytics.domain.assets.parameterization.Fees
import org.pillarone.riskanalytics.domain.assets.parameterization.InitialCash
import org.pillarone.riskanalytics.domain.assets.parameterization.YieldCurveChoice
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

class AssetModel extends StochasticModel {

    GlobalParameters globalParameters
    DynamicBonds bonds
    AssetEngine engine
    YieldCurveChoice yieldCurve
    InitialCash treasury
    Fees fees


    public void initComponents() {
        globalParameters = new GlobalParameters()
        bonds = new DynamicBonds()
        engine = new AssetEngine()
        yieldCurve = new YieldCurveChoice()
        fees = new Fees()
        treasury = new InitialCash()

        addStartComponent fees
        addStartComponent treasury
        addStartComponent bonds
        addStartComponent yieldCurve
    }

    public void wireComponents() {
        engine.inBondParameters = bonds.outBondParameters
        engine.inYieldModellingChoices = yieldCurve.outYieldModellingChoices
        engine.inCashParameters = treasury.outCashParameters
        engine.inFeesParameters = fees.outFeesParameters
    }

    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        return new ContinuousPeriodCounter(beginOfFirstPeriod, Period.years(1))
    }

    boolean requiresStartDate() {
        return true
    }


}
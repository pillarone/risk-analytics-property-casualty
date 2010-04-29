package org.pillarone.riskanalytics.domain.assets.parameterization;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.assets.*;

/**
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
public class YieldCurveChoice extends Component {


    private IModellingStrategy parmModellingStrategy = TermStructureType.getStrategy(TermStructureType.CONSTANT,
        ArrayUtils.toMap(new Object[][]{{"rate", 0.0}}));

    private PacketList<YieldModellingChoices> outYieldModellingChoices = new PacketList<YieldModellingChoices>(YieldModellingChoices.class);

    protected void doCalculation() {
        if (getParmModellingStrategy() == null) {
            throw new IllegalStateException("A curve has to be chosen");
        }

        if (parmModellingStrategy.getType() == TermStructureType.CIR) {
            CIRYieldModellingChoices packet = new CIRYieldModellingChoices();
            packet.setYieldCurveType(TermStructureType.CIR);
            packet.setVolatility((Double) parmModellingStrategy.getParameters().get("volatility"));
            packet.setMeanReversionParameter((Double) parmModellingStrategy.getParameters().get("meanReversionParameter"));
            packet.setRiskAversionParameter((Double) parmModellingStrategy.getParameters().get("riskAversionParameter"));
            packet.setInitialInterestRate((Double) parmModellingStrategy.getParameters().get("initialInterestRate"));
            packet.setLongRunMean((Double) parmModellingStrategy.getParameters().get("longRunMean"));
            outYieldModellingChoices.add(packet);
        } else if (parmModellingStrategy.getType() == TermStructureType.CONSTANT) {
            ConstantYieldModellingChoices packet = new ConstantYieldModellingChoices();
            packet.setYieldCurveType((TermStructureType) parmModellingStrategy.getType());
            packet.setRate((Double) parmModellingStrategy.getParameters().get("rate"));
        } else {
            throw new IllegalArgumentException("not yet implemented or doesn't exist" + parmModellingStrategy.getType());
        }
    }

    public IModellingStrategy getParmModellingStrategy() {
        return parmModellingStrategy;
    }

    public void setParmModellingStrategy(IModellingStrategy parmModellingStrategy) {
        this.parmModellingStrategy = parmModellingStrategy;
    }

    public PacketList<YieldModellingChoices> getOutYieldModellingChoices() {
        return outYieldModellingChoices;
    }

    public void setOutYieldModellingChoices(PacketList<YieldModellingChoices> outYieldModellingChoices) {
        this.outYieldModellingChoices = outYieldModellingChoices;
    }
}

package models.example

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.examples.groovy.FlexibleIndexProvider
import org.pillarone.riskanalytics.domain.examples.groovy.DynamicPremiumCalculation
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ExampleModel extends StochasticModel {

    GlobalParameters globalParameters
    FlexibleIndexProvider indexProvider
    DynamicPremiumCalculation premiumCalculation

    void initComponents() {
        globalParameters = new GlobalParameters()
        indexProvider = new FlexibleIndexProvider()
        premiumCalculation = new DynamicPremiumCalculation()

        addStartComponent indexProvider
    }

    void wireComponents() {
        premiumCalculation.inIndex = indexProvider.outIndex
    }
}

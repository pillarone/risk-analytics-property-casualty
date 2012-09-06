package models.profiling

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.FrequencyClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters

class FrequencyClaimsGeneratorModel extends StochasticModel {

    GlobalParameters globalParameters
    FrequencyClaimsGenerator frequencyClaimsGenerator

    public void initComponents() {
        globalParameters = new GlobalParameters()
        frequencyClaimsGenerator = new FrequencyClaimsGenerator()
        allComponents << frequencyClaimsGenerator
        addStartComponent frequencyClaimsGenerator
    }

    public void wireComponents() {

    }

}
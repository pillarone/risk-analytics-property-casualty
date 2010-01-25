package models.profiling

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.claims.FrequencyClaimsGenerator

class FrequencyClaimsGeneratorModel extends StochasticModel {

    final FrequencyClaimsGenerator frequencyClaimsGenerator

    public void initComponents() {
        frequencyClaimsGenerator = new FrequencyClaimsGenerator()
        allComponents << frequencyClaimsGenerator
        addStartComponent frequencyClaimsGenerator
    }

    public void wireComponents() {

    }

}
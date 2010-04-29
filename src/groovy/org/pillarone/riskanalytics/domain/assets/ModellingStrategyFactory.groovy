package org.pillarone.riskanalytics.domain.assets

/**
 * @deprecated use TermStructureType.getStrategy
 * @author cyril (dot) neyme (at) kpmg (dot) fr
 */
@Deprecated
public class ModellingStrategyFactory {

    @Deprecated
    static IModellingStrategy getModellingStrategy(TermStructureType type, Map parameters) {
        TermStructureType.getStrategy(type, parameters)
    }

}
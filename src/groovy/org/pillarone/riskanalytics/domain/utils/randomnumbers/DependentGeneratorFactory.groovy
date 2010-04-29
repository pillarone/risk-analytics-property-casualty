package org.pillarone.riskanalytics.domain.utils.randomnumbers

/**
 * @deprecated use DependencyType.getStrategy
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class DependentGeneratorFactory {

    @Deprecated
    static IMultiRandomGenerator getGenerator(DependencyType type, Map parameters) {
        DependencyType.getStrategy(type, parameters)
    }
    
}
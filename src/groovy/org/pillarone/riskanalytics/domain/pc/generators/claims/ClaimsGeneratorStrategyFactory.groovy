package org.pillarone.riskanalytics.domain.pc.generators.claims

/**
 * @deprecated access directly method getStrategy in CGT
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class ClaimsGeneratorStrategyFactory {

    @Deprecated
    static IClaimsGeneratorStrategy getStrategy(ClaimsGeneratorType type, Map parameters) {
        return ClaimsGeneratorType.getStrategy(type, parameters)
    }
    
}
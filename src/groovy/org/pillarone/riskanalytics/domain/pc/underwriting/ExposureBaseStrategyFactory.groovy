package org.pillarone.riskanalytics.domain.pc.underwriting

/**
 * @deprecated use ExposureBaseType.getStrategy
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
public class ExposureBaseStrategyFactory {

    @Deprecated
    static IExposureBaseStrategy getStrategy(ExposureBaseType type, Map parameters) {
        ExposureBaseType.getStrategy(type, parameters)
    }

}
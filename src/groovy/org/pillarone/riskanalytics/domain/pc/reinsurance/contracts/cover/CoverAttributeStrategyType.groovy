package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.reserves.IReserveMarker
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final CoverAttributeStrategyType ALL = new CoverAttributeStrategyType("all", "ALL", ['reserves': IncludeType.NOTINCLUDED])
    public static final CoverAttributeStrategyType NONE = new CoverAttributeStrategyType("none", "NONE", [:])
    public static final CoverAttributeStrategyType LINESOFBUSINESS = new CoverAttributeStrategyType(
            'lines of business', 'LINESOFBUSINESS',
            ['lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Lines'], LobMarker.class)])
    public static final CoverAttributeStrategyType PERILS = new CoverAttributeStrategyType(
            'perils', 'PERILS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], PerilMarker.class)])
    public static final CoverAttributeStrategyType RESERVES = new CoverAttributeStrategyType(
            'reserves', 'RESERVES',
            ['reserves':new ComboBoxTableMultiDimensionalParameter([], ['Covered Reserves'], IReserveMarker.class)])
    public static final CoverAttributeStrategyType LINESOFBUSINESSPERILS = new CoverAttributeStrategyType(
            'lines of business, perils', 'LINESOFBUSINESSPERILS',
            ['connection': LogicArguments.AND,
             'lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Lines'], LobMarker.class),
             'perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], PerilMarker.class)])
    public static final CoverAttributeStrategyType LINESOFBUSINESSRESERVES = new CoverAttributeStrategyType(
            'lines of business, reserves', 'LINESOFBUSINESSRESERVES',
            ['connection': LogicArguments.AND,
             'lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Lines'], LobMarker.class),
             'reserves':new ComboBoxTableMultiDimensionalParameter([], ['Covered Reserves'], IReserveMarker.class)])

    public static final all = [ALL, NONE, LINESOFBUSINESS, PERILS, LINESOFBUSINESSPERILS, RESERVES, LINESOFBUSINESSRESERVES]

    protected static Map types = [:]
    static {
        CoverAttributeStrategyType.all.each {
            CoverAttributeStrategyType.types[it.toString()] = it
        }
    }

    private CoverAttributeStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static CoverAttributeStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static ICoverAttributeStrategy getStrategy(CoverAttributeStrategyType type, Map parameters) {
        ICoverAttributeStrategy commissionStrategy ;
        switch (type) {
            case CoverAttributeStrategyType.ALL:
                commissionStrategy = new AllCoverAttributeStrategy(reserves : (IncludeType) parameters['reserves'])
                break
            case CoverAttributeStrategyType.NONE:
                commissionStrategy = new NoneCoverAttributeStrategy()
                break
            case CoverAttributeStrategyType.LINESOFBUSINESS:
                commissionStrategy = new LineOfBusinessCoverAttributeStrategy(lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'])
                break
            case CoverAttributeStrategyType.PERILS:
                commissionStrategy = new PerilsCoverAttributeStrategy(perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'])
                break
            case CoverAttributeStrategyType.RESERVES:
                commissionStrategy = new ReservesCoverAttributeStrategy(reserves: (ComboBoxTableMultiDimensionalParameter) parameters['reserves'])
                break
            case CoverAttributeStrategyType.LINESOFBUSINESSPERILS:
                commissionStrategy = new LineOfBusinessPerilsCoverAttributeStrategy(
                        lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'],
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'],
                        connection: (LogicArguments) parameters['connection']
                )
                break
            case CoverAttributeStrategyType.LINESOFBUSINESSRESERVES:
                commissionStrategy = new LineOfBusinessReservesCoverAttributeStrategy(
                        lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'],
                        reserves: (ComboBoxTableMultiDimensionalParameter) parameters['reserves'],
                        connection: (LogicArguments) parameters['connection']
                )
                break
        }
        return commissionStrategy;
    }
}

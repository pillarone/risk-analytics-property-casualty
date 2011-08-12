package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover

import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.constants.IncludeType
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.company.ICompanyMarker

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class CompanyCoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final CompanyCoverAttributeStrategyType ALL = new CompanyCoverAttributeStrategyType("all", "ALL", ['reserves': IncludeType.NOTINCLUDED])
    public static final CompanyCoverAttributeStrategyType NONE = new CompanyCoverAttributeStrategyType("none", "NONE", [:])
    public static final CompanyCoverAttributeStrategyType LINESOFBUSINESS = new CompanyCoverAttributeStrategyType(
            'lines of business', 'LINESOFBUSINESS',
            ['lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker.class)])
    public static final CompanyCoverAttributeStrategyType PERILS = new CompanyCoverAttributeStrategyType(
            'perils', 'PERILS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], IPerilMarker.class)])
    public static final CompanyCoverAttributeStrategyType RESERVES = new CompanyCoverAttributeStrategyType(
            'reserves', 'RESERVES',
            ['reserves':new ComboBoxTableMultiDimensionalParameter([], ['Covered Reserves'], IReserveMarker.class)])
    public static final CompanyCoverAttributeStrategyType LINESOFBUSINESSPERILS = new CompanyCoverAttributeStrategyType(
            'lines of business, perils', 'LINESOFBUSINESSPERILS',
            ['connection': LogicArguments.AND,
             'lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker.class),
             'perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], IPerilMarker.class)])
    public static final CompanyCoverAttributeStrategyType LINESOFBUSINESSRESERVES = new CompanyCoverAttributeStrategyType(
            'lines of business, reserves', 'LINESOFBUSINESSRESERVES',
            ['connection': LogicArguments.AND,
             'lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Segments'], ISegmentMarker.class),
             'reserves':new ComboBoxTableMultiDimensionalParameter([], ['Covered Reserves'], IReserveMarker.class)])
    public static final CompanyCoverAttributeStrategyType COMPANIES = new CompanyCoverAttributeStrategyType(
            'companies', 'COMPANIES',
            ['companies':new ComboBoxTableMultiDimensionalParameter([], ['Covered Companies'], ICompanyMarker.class)])

    public static final all = [ALL, NONE, LINESOFBUSINESS, PERILS, LINESOFBUSINESSPERILS, RESERVES, LINESOFBUSINESSRESERVES, COMPANIES]

    protected static Map types = [:]
    static {
        CompanyCoverAttributeStrategyType.all.each {
            CompanyCoverAttributeStrategyType.types[it.toString()] = it
        }
    }

    private CompanyCoverAttributeStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static CompanyCoverAttributeStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static ICoverAttributeStrategy getStrategy(CompanyCoverAttributeStrategyType type, Map parameters) {
        ICoverAttributeStrategy coverStrategy ;
        switch (type) {
            case CompanyCoverAttributeStrategyType.ALL:
                coverStrategy = new AllCompanyCoverAttributeStrategy(reserves : (IncludeType) parameters['reserves'])
                break
            case CompanyCoverAttributeStrategyType.NONE:
                coverStrategy = new NoneCompanyCoverAttributeStrategy()
                break
            case CompanyCoverAttributeStrategyType.LINESOFBUSINESS:
                coverStrategy = new LineOfBusinessCompanyCoverAttributeStrategy(lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'])
                break
            case CompanyCoverAttributeStrategyType.PERILS:
                coverStrategy = new PerilsCompanyCoverAttributeStrategy(perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'])
                break
            case CompanyCoverAttributeStrategyType.RESERVES:
                coverStrategy = new ReservesCompanyCoverAttributeStrategy(reserves: (ComboBoxTableMultiDimensionalParameter) parameters['reserves'])
                break
            case CompanyCoverAttributeStrategyType.LINESOFBUSINESSPERILS:
                coverStrategy = new LineOfBusinessPerilsCompanyCoverAttributeStrategy(
                        lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'],
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'],
                        connection: (LogicArguments) parameters['connection']
                )
                break
            case CompanyCoverAttributeStrategyType.LINESOFBUSINESSRESERVES:
                coverStrategy = new LineOfBusinessReservesCompanyCoverAttributeStrategy(
                        lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'],
                        reserves: (ComboBoxTableMultiDimensionalParameter) parameters['reserves'],
                        connection: (LogicArguments) parameters['connection']
                )
                break
            case CompanyCoverAttributeStrategyType.COMPANIES:
                coverStrategy = new CompaniesCompanyCoverAttributeStrategy(companies: (ComboBoxTableMultiDimensionalParameter) parameters['companies'])
                break
        }
        return coverStrategy;
    }
}


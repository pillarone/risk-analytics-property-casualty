package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover

/** use contracts.cashflow.cover.{AllCoverAttributeStrategy,CoverAttributeStrategyType}, otherwise contracts.cover.*  */

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.cover.AllCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.*

/**
 * This class (reinsurance&#46;contracts&#46;cashflow&#46;cover&#46;CoverAttributeStrategyType) is similar
 * to its homonym in package contracts&#46;cover, but doesn't offer cover strategies based on reserves
 * //todo(bgi): see if the HTML ascii codes above display as dots in the javadoc.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */

class CoverAttributeStrategyType extends AbstractParameterObjectClassifier {

    public static final CoverAttributeStrategyType ALL = new CoverAttributeStrategyType("all", "ALL", [:])
    public static final CoverAttributeStrategyType NONE = new CoverAttributeStrategyType("none", "NONE", [:])
    public static final CoverAttributeStrategyType LINESOFBUSINESS = new CoverAttributeStrategyType(
            'lines of business', 'LINESOFBUSINESS',
            ['lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Lines'], LobMarker.class)])
    public static final CoverAttributeStrategyType PERILS = new CoverAttributeStrategyType(
            'perils', 'PERILS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], PerilMarker.class)])
    public static final CoverAttributeStrategyType LINESOFBUSINESSPERILS = new CoverAttributeStrategyType(
            'lines of business, perils', 'LINESOFBUSINESSPERILS',
            ['connection': LogicArguments.AND,
             'lines':new ComboBoxTableMultiDimensionalParameter([], ['Covered Lines'], LobMarker.class),
             'perils':new ComboBoxTableMultiDimensionalParameter([], ['Covered Perils'], PerilMarker.class)])

    public static final all = [ALL, NONE, LINESOFBUSINESS, PERILS, LINESOFBUSINESSPERILS]

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
                commissionStrategy = new AllCoverAttributeStrategy()
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
            case CoverAttributeStrategyType.LINESOFBUSINESSPERILS:
                commissionStrategy = new LineOfBusinessPerilsCoverAttributeStrategy(
                        lines: (ComboBoxTableMultiDimensionalParameter) parameters['lines'],
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'],
                        connection: (LogicArguments) parameters['connection']
                )
                break
        }
        return commissionStrategy;
    }
}
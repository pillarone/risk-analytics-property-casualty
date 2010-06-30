package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType

import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationErrorImpl

import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.generators.claims.DevelopedTypableClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.lob.ConfigurableLobWithReserves
import org.pillarone.riskanalytics.domain.pc.generators.claims.ClaimsGeneratorType
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SurplusStrategyValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(SurplusStrategyValidator)

    private static final PATH_SEPARATOR = ':'

    public SurplusStrategyValidator() {
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

//        getContract(parameters, ReinsuranceContractType.class, ReinsuranceContractType.SURPLUS)
//        getPerils(parameters, RiskAllocatorType.class)


        List<ParameterHolder> surplusPaths = findContractPaths(parameters, ReinsuranceContractType.SURPLUS)
        Map<String, ParameterHolder> coveredComponents = findCoveredPerils(parameters, surplusPaths)
        errors.addAll exposureInformationAvailable(parameters, coveredComponents)
//        coveredComponents << findCoveredLinesPerils()
        /*for (IReinsuranceContractMarker cover : covers) {
            List<PerilMarker> coverPerils = findClaimsGenerators(cover)
            for (PerilMarker coverPeril : coverPerils) {
                if (!coverPeril.hasProperty('parmAssociateExposureInfo')) {
                    errors << error("coverPeril.without.exposure.info", coverPeril.getNormalizedName(), cover)
                    errors << error("no.exposure.association.possible", null, coverPeril)
                    continue
                }
                def exposure = coverPeril.parmAssociateExposureInfo
                if (!exposure instanceof IRiskAllocatorStrategy) {
                    errors << error("coverPeril.without.exposure.info", coverPeril.getNormalizedName(), cover)
                    errors << error("no.exposure.association.possible", null, coverPeril)
                    continue
                }
                if (exposure.type == RiskAllocatorType.NONE) {
                    errors << error("coverPeril.without.exposure.info", coverPeril.getNormalizedName(), cover)
                    errors << error("no.exposure.associated", null, coverPeril)
                }
            }
        }*/
        errors
    }

    private ParameterValidationError error(String message, String args, def parameter) {
        def parameterValidationError = new ParameterValidationErrorImpl(message, [args])
        parameterValidationError.path = parameter.path
        parameterValidationError
    }

    private static List<ParameterHolder> findContractPaths(List<ParameterHolder> parameters, ReinsuranceContractType contractType) {
        List<ParameterHolder> contractPaths = []
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ReinsuranceContractType && classifier == contractType) {
                    contractPaths << parameter
                }
            }
        }
        return contractPaths
    }

    /**
     *
     * @param parameters
     * @param perilNames key: perilName, value ReinsuranceContractType ParameterHolder
     * @return
     */
    private List<ParameterValidationError> exposureInformationAvailable(List<ParameterHolder> parameters,
                                                                        Map<String, ParameterHolder> perilNames) {
        List<ParameterValidationError> errors = []
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof RiskAllocatorType) {
                    if (classifier.equals(RiskAllocatorType.NONE)) {
                        for (Map<String, ParameterHolder> perilName : perilNames) {
                            if (parameter.path.contains(perilName.key)) {
                                errors << error("coverPeril.without.exposure.info", perilName.key, perilName.value)
                                errors << error("no.exposure.associated", null, parameter)
                            }
                        }
                    }
                }
            }
        }
        return errors
    }

    /**
     *
     * @param parameters
     * @param ReinsuranceContractType ParameterHolder (required for error msg)
     * @return key: perilName, value: ReinsuranceContractType ParameterHolder
     */
    private static Map<String, ParameterHolder> findCoveredPerils(List<ParameterHolder> parameters,
                                                                  List<ParameterHolder> riContractPaths) {
        if (riContractPaths.empty) return
        Map<String, ParameterHolder> perilComponentName = [:]
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CoverAttributeStrategyType) {
                    ParameterHolder coveredContract
                    for (ParameterHolder contract : riContractPaths) {
                        if ((contract.path - ':parmContractStrategy').equals(parameter.path - ':parmCover')) {
                            coveredContract = contract
                            break
                        }
                    }

                    if (classifier == CoverAttributeStrategyType.PERILS) {
                        Map<String, DevelopedTypableClaimsGenerator> availablePerils = parameter.classifierParameters['perils']?.value?.comboBoxValues
                        if (availablePerils.size()) {
                            Set<String> coveredPerils = new HashSet<String>()
                            for (String perilName : parameter.classifierParameters['perils']?.value?.values) {
                                PerilMarker peril = availablePerils.get(perilName)
                                coveredPerils.add(peril.name)
                            }
                            for (String peril : coveredPerils) {
                                perilComponentName.put(peril, coveredContract)
                            }
                        }
                    }

                    else if (classifier == CoverAttributeStrategyType.LINESOFBUSINESS) {
//                        perilComponentName << parameter.classifierParameters[0].value.comboBoxValues.value
                        Map<String, ConfigurableLobWithReserves> availableLines = parameter.classifierParameters['lines']?.value?.comboBoxValues
                        if (availableLines.size()) {
                            Set<String> coveredLines = new HashSet<String>()
                            Map<String, ParameterHolder> lineComponentName = [:]
                            for (String lineName : parameter.classifierParameters['lines']?.value?.values) {
                                coveredLines.add(availableLines.get(lineName))
                                for (ParameterHolder param in parameters) {
                                    if (param instanceof MultiDimensionalParameterHolder) {
                                        if (param.path.contains(availableLines.get(lineName).name + ':subClaimsFilter:parmPortions')) {
                                            for (String perilName : param.value.values[0]) {
                                                perilComponentName.put(getTechnicalName(perilName, 'sub'), coveredContract)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if (classifier == CoverAttributeStrategyType.LINESOFBUSINESSPERILS) {
                        perilComponentName << parameter.classifierParameters[0].value.comboBoxValues.value
                    }
                    else if (classifier == CoverAttributeStrategyType.ALL) {
                        for (String perilName : getAllPerils(parameters)) {
                            perilComponentName.put(perilName, coveredContract)
                        }
                    }
                }
            }
        }
        return perilComponentName
    }

    // classifierInstance CoverAttributeStrategyType
    private static Map<String, ParameterHolder> getContract(ParameterHolder parameters, Class classifierInstance,
                                                            ReinsuranceContractType contractType) {
        Map<String, ParameterHolder> contracts = [:]
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier.class.equals(classifierInstance) && classifier == contractType) {
                    String[] pathElements = parameter.path.split(PATH_SEPARATOR)
                    String technicalPerilName = pathElements[pathElements.size() - 2]
                    contracts.put(technicalPerilName, parameter)
                }
            }
        }
        return contracts
    }

    // classifierInstance CoverAttributeStrategyType
    private static Map<String, ParameterHolder> getContract(ParameterHolder parameters, Class classifierInstance) {
        Map<String, ParameterHolder> contracts = [:]
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier.class.equals(classifierInstance)) {
                    String[] pathElements = parameter.path.split(PATH_SEPARATOR)
                    String technicalPerilName = pathElements[pathElements.size() - 2]
                    contracts.put(technicalPerilName, parameter)
                }
            }
        }
        return contracts
    }


    private static Map<String, ParameterHolder> getPerils(Map<String, ParameterHolder> perils, List<String> filter) {
        Map<String, ParameterHolder> filteredPerils = [:]
        for (String peril : filter) {
            filteredPerils.put(peril, filteredPerils[peril])
        }
        return filteredPerils
    }

    // classifierInstance RiskAllocatorType
    private static Map<String, ParameterHolder> getPerils(ParameterHolder parameters, Class classifierInstance) {
        Map<String, ParameterHolder> perils = [:]
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier.class.equals(classifierInstance)) {
                    String[] pathElements = parameter.path.split(':')
                    String technicalPerilName = pathElements[pathElements.size() - 2]
                    perils.put(technicalPerilName, parameter)
                }
            }
        }
        return perils
    }


    private static List<String> getPerils(List<ParameterHolder> parameters, List<String> lines, Map<String, List<String>> perilsPerLineOfBusiness) {
        List<String> perils = []
        for (String line : lines) {
            perils << perilsPerLineOfBusiness[line]
        }
        return perils
    }

    private static List<String> getPerils(List<ParameterHolder> parameters, List<String> lines) {
        return getPerils(parameters, lines, getPerilsPerLineOfBusiness(parameters))
    }

    /**
     *
     * @param parameters
     * @return key: line of business technical name, value: list of covered perils (technical name)
     */
    private static Map<String, List<String>> getPerilsPerLineOfBusiness(List<ParameterHolder> parameters) {
        Map<String, List<String>> perilsPerLineOfBusiness = new HashMap<String, List<String>>()
        for (ParameterHolder parameter in parameters) {
            if (parameter.path.contains(':subClaimsFilter:parmPortions')) {
                String[] pathElements = parameter.path.split(':')
                String technicalLineName = pathElements[pathElements.size() - 3]
                List<String> coveredPerils = []
                for (String perilName : parameter.value.values[0]) {
                    coveredPerils << getTechnicalName(perilName, 'sub')
                }
                if (!coveredPerils.empty) {
                    perilsPerLineOfBusiness.put(technicalLineName, coveredPerils)
                }
            }
        }
        return perilsPerLineOfBusiness
    }

    /**
     *
     * @param parameters
     * @return technical names of peril components containing ClaimsGeneratorType
     */
    private static List<String> getAllPerils(List<ParameterHolder> parameters) {
        List<String> perilTechnicalNames = []
        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof ClaimsGeneratorType) {
                    String[] pathElements = parameter.path.split(':')
                    String perilName = pathElements[pathElements.size() - 2]
                    perilTechnicalNames.add perilName
                }
            }
        }
        return perilTechnicalNames
    }

//    private static List<PerilMarker> findClaimsGenerators(IReinsuranceContractMarker contract) {
//        List<PerilMarker> coveredPerils = []
////        private ICoverAttributeStrategy parmCover = CoverAttributeStrategyType.getStrategy(
////                CoverAttributeStrategyType.ALL, ArrayUtils.toMap(new Object[][]{{"reserves", IncludeType.NOTINCLUDED}}));
//        if (contract.hasProperty('parmCover')) {
//            if (contract.parmCover.equals(CoverAttributeStrategyType.PERILS)) {
//                coveredPerils << contract.parmCover.getColumnByName('Covered Perils')
//            }
//            else if (contract.parmCover.equals(CoverAttributeStrategyType.LINESOFBUSINESS)) {
//
//            }
//            else if (contract.parmCover.equals(CoverAttributeStrategyType.LINESOFBUSINESSPERILS)) {
//
//            }
//        }
//        coveredPerils
//    }

    public static String getTechnicalName(String displayName, String prefix) {
        StringBuffer technicalNameBuffer = new StringBuffer()
        technicalNameBuffer.append prefix
        String[] singleWords = displayName.split(' ')
        for (String word : singleWords) {
            technicalNameBuffer.append word[0].toUpperCase()
            technicalNameBuffer.append word.substring(1)
        }
        return technicalNameBuffer.toString()
    }
}

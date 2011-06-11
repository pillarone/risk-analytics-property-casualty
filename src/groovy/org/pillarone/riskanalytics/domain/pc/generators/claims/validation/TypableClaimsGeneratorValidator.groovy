package org.pillarone.riskanalytics.domain.pc.generators.claims.validation

import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.claims.RiskAllocatorType
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationImpl

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class TypableClaimsGeneratorValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(TypableClaimsGeneratorValidator)
    private AbstractParameterValidationService validationService

    public ClaimsGeneratorValidator() {
        validationService = new ParameterValidationServiceImpl()
    }

    List<ParameterValidation> validate(List<ParameterHolder> parameters) {

        List<ParameterValidation> errors = []
        Map<String, RiskAllocatorType> associateExposureInfoPerClaimsGenerator = [:]
        Map<String, Boolean> underwritingInfoPerClaimsGenerator = [:]

        for (ParameterHolder parameter in parameters) {
            if (LOG.isDebugEnabled()) {
                LOG.debug "validating ${parameter.path}"
            }

            if (parameter instanceof ParameterObjectParameterHolder && parameter.classifier instanceof RiskAllocatorType) {
                associateExposureInfoPerClaimsGenerator[parameter.path - ':parmAssociateExposureInfo'] = (RiskAllocatorType) parameter.classifier
            }
            else if (parameter instanceof MultiDimensionalParameterHolder && parameter.value instanceof ComboBoxTableMultiDimensionalParameter) {
                if (parameter.path.contains('claimsGenerators') && parameter.value.markerClass.is(IUnderwritingInfoMarker)) {
                    boolean hasSelectedUnderwritingInfo = (parameter.value.values.size() > 0
                            && ((parameter.value.values[0] instanceof String && parameter.value.values[0].length() > 0)))
                    underwritingInfoPerClaimsGenerator[parameter.path - ':parmUnderwritingInformation'] = hasSelectedUnderwritingInfo
                }
            }
        }

        for (String claimsGeneratorPath: associateExposureInfoPerClaimsGenerator.keySet()) {
            RiskAllocatorType allocatorType = associateExposureInfoPerClaimsGenerator[claimsGeneratorPath]
            boolean hasSelectedUnderwritingInfo = underwritingInfoPerClaimsGenerator[claimsGeneratorPath]
            if (!allocatorType.equals(RiskAllocatorType.NONE) && !hasSelectedUnderwritingInfo) {
                ParameterValidationImpl error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                errors << error
                error.path = claimsGeneratorPath + ':parmAssociateExposureInfo'
                error = new ParameterValidationImpl(ValidationType.ERROR,
                        'associate.exposure.info.requires.underwriting.info', [allocatorType])
                errors << error
                error.path = claimsGeneratorPath + ':parmUnderwritingInformation'
            }
        }

        return errors
    }
}

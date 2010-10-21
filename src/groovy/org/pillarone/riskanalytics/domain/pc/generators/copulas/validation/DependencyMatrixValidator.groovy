package org.pillarone.riskanalytics.domain.pc.generators.copulas.validation

import org.pillarone.riskanalytics.core.parameterization.validation.IParameterizationValidator
import org.pillarone.riskanalytics.core.parameterization.validation.AbstractParameterValidationService
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidationError
import org.pillarone.riskanalytics.domain.utils.validation.ParameterValidationServiceImpl
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.generators.copulas.CopulaType
import org.pillarone.riskanalytics.domain.pc.generators.copulas.PerilCopulaType
import cern.colt.matrix.DoubleMatrix2D
import cern.colt.matrix.impl.DenseDoubleMatrix2D
import cern.colt.matrix.linalg.EigenvalueDecomposition
import cern.colt.matrix.DoubleMatrix1D

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class DependencyMatrixValidator implements IParameterizationValidator {

    private static Log LOG = LogFactory.getLog(DependencyMatrixValidator)
    private AbstractParameterValidationService validationService

    public DependencyMatrixValidator() {
        validationService = new ParameterValidationServiceImpl()
        registerConstraints()
    }

    List<ParameterValidationError> validate(List<ParameterHolder> parameters) {

        List<ParameterValidationError> errors = []

        for (ParameterHolder parameter in parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                IParameterObjectClassifier classifier = parameter.getClassifier()
                if (classifier instanceof CopulaType) {
                    List<String> names = classifier.getParameterNames()
                    if (names.contains("dependencyMatrix")) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug "validating ${parameter.path}"
                        }

                        def currentErrors = validationService.validate(classifier, parameter.getParameterMap())
                        currentErrors*.path = parameter.path
                        errors.addAll(currentErrors)

                    }
                }
                errors.addAll(validate(parameter.classifierParameters.values().toList()))
            }
        }
        return errors
    }

    private void registerConstraints() {

        validationService.register(PerilCopulaType.T) {Map type ->
            List<List<Double>> values = type.dependencyMatrix.getValues();
            DenseDoubleMatrix2D SIGMA = new DenseDoubleMatrix2D((double[][]) values);
            DoubleMatrix2D SIGMAtranspose = SIGMA.viewDice();
            if (!SIGMAtranspose.equals(SIGMA)) {
                return ["t.copula.strategy.dependency.matrix.non.symmetric", values]
            }
            EigenvalueDecomposition eigenvalueDecomp = new EigenvalueDecomposition(SIGMA);
            DoubleMatrix1D eigenvalues = eigenvalueDecomp.getRealEigenvalues();
            eigenvalues.viewSorted();
            if (eigenvalues.get(0) <= 0) {
                return ["t.copula.strategy.dependency.matrix.non.positive.definite", values]
            }
            return true
        }

        validationService.register(PerilCopulaType.T) {Map type ->
            List<List<Double>> values = type.dependencyMatrix.getValues();
            List<Double> diag = new ArrayList<Double>();
            for (int i = 0; i < values.size(); i++) {
                diag.add(values.get(i).get(i))
            }
            if (!(diag.min() == 1d && diag.max() == 1d)) {
                return ["t.copula.strategy.dependency.matrix.invalid.diagonal", values]
            }
            return true
        }
        validationService.register(PerilCopulaType.NORMAL) {Map type ->
            List<List<Double>> values = type.dependencyMatrix.getValues();
            DenseDoubleMatrix2D SIGMA = new DenseDoubleMatrix2D((double[][]) values);
            DoubleMatrix2D SIGMAtranspose = SIGMA.viewDice();
            if (!SIGMAtranspose.equals(SIGMA)) {
                return ["normal.copula.strategy.dependency.matrix.non.symmetric", values]
            }
            EigenvalueDecomposition eigenvalueDecomp = new EigenvalueDecomposition(SIGMA);
            DoubleMatrix1D eigenvalues = eigenvalueDecomp.getRealEigenvalues();
            eigenvalues.viewSorted();
            if (eigenvalues.get(0) <= 0) {
                return ["normal.copula.strategy.dependency.matrix.non.positive.definite", values]
            }
            return true
        }

        validationService.register(PerilCopulaType.NORMAL) {Map type ->
            List<List<Double>> values = type.dependencyMatrix.getValues();
            List<Double> diag = new ArrayList<Double>();
            for (int i = 0; i < values.size(); i++) {
                diag.add(values.get(i).get(i))
            }
            if (!(diag.min() == 1d && diag.max() == 1d)) {
                return ["normal.copula.strategy.dependency.matrix.invalid.diagonal", values]
            }
            return true
        }

    }
}

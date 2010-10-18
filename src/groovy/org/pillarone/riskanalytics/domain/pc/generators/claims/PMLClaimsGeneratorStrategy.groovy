package org.pillarone.riskanalytics.domain.pc.generators.claims

import org.pillarone.riskanalytics.domain.utils.DistributionModified
import org.pillarone.riskanalytics.domain.utils.DistributionModifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.RandomDistribution
import org.pillarone.riskanalytics.domain.pc.constants.Exposure
import org.pillarone.riskanalytics.domain.utils.constraints.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class PMLClaimsGeneratorStrategy implements IClaimsGeneratorStrategy {

    ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter([[0d],[0d]], Arrays.asList("return period", "maximum claim"),ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
    DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.PML
    }

    public Map getParameters() {
        ['pmlData': pmlData, 'claimsSizeModification': claimsSizeModification]
    }


    public DistributionModified getClaimsSizeModification() {
        return claimsSizeModification
    }

    Exposure getClaimsSizeBase(){};

    RandomDistribution getClaimsSizeDistribution(){};

    }


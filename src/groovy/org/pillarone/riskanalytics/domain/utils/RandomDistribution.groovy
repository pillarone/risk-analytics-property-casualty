package org.pillarone.riskanalytics.domain.utils

import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import umontreal.iro.lecuyer.probdist.Distribution

class RandomDistribution implements IParameterObject {

    Distribution distribution
    DistributionType type
    Map parameters

    DistributionType getType() {
        type
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder()
        builder.append(distribution.class)

        def sortedParameters = parameters.entrySet().sort {Map.Entry it -> it.key}

        sortedParameters.each {Map.Entry entry ->
            builder.append(entry.value)
        }
        builder.toHashCode()
    }

    public boolean equals(Object obj) {
        if (obj instanceof RandomDistribution) {
            if (!distribution.class.equals(obj.distribution.class)) {
                return false
            }
            boolean parametersEqual = true
            parameters.keySet().each {
                if (!parameters[it].equals(obj.parameters[it])) {
                    parametersEqual = false
                }
            }
            return parametersEqual
        } else {
            return false
        }

    }


}
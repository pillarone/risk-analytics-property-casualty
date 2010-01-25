package org.pillarone.riskanalytics.domain.utils

import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.parameterization.IParameterObject

class DistributionModified implements IParameterObject {

    DistributionModifier type
    Map parameters

    DistributionModifier getType() {
        type
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder()
        builder.append(type.toString())

        def sortedParameters = parameters.entrySet().sort {Map.Entry it -> it.key}

        sortedParameters.each {Map.Entry entry ->
            builder.append(entry.value)
        }
        builder.toHashCode()
    }

    public boolean equals(Object obj) {
        if (obj instanceof DistributionModified) {
            if (!type.toString().equals(obj.type.toString())) {
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
package org.pillarone.riskanalytics.domain.pc.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder;
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder;
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder;
import org.pillarone.riskanalytics.domain.pc.generators.fac.FacShareDistributions;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.underwriting.RiskBands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ValidatorUtils {

    private static Log LOG = LogFactory.getLog(ValidatorUtils.class);

    public static Map<String, TableMultiDimensionalParameter> getUnderwritingInfos(List<ParameterHolder> parameters) {
        Map<String, TableMultiDimensionalParameter> underwritingInfos = new HashMap<String, TableMultiDimensionalParameter>();
        for (ParameterHolder parameter : parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = (AbstractMultiDimensionalParameter) parameter.getBusinessObject();
                if (value instanceof TableMultiDimensionalParameter) {
                    List<String> titles = value.getColumnNames();
                    if (titles.equals(RiskBands.getColumnTitles())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("validating " + parameter.getPath());
                        }
                        underwritingInfos.put(parameter.getPath(), (TableMultiDimensionalParameter) value);
                    }
                }
            }
        }
        return underwritingInfos;
    }

    public static Map<String, TableMultiDimensionalParameter> getTreatyAllocationFac(List<ParameterHolder> parameters) {
        Map<String, TableMultiDimensionalParameter> underwritingInfos = new HashMap<String, TableMultiDimensionalParameter>();
        for (ParameterHolder parameter : parameters) {
            if (parameter instanceof MultiDimensionalParameterHolder) {
                AbstractMultiDimensionalParameter value = (AbstractMultiDimensionalParameter) parameter.getBusinessObject();
                if (value instanceof TableMultiDimensionalParameter) {
                    List<String> titles = value.getColumnNames();
                    if (titles.equals(FacShareDistributions.getColumnTitles())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("validating " + parameter.getPath());
                        }
                        underwritingInfos.put(parameter.getPath(), (TableMultiDimensionalParameter) value);
                    }
                }
            }
        }
        return underwritingInfos;
    }

    public static Map<String, IReinsuranceContractStrategy> getReinsuranceContracts(List<ParameterHolder> parameters,
        List<Class<? extends IReinsuranceContractStrategy>> contractTypes) {
        Map<String, IReinsuranceContractStrategy> contracts = new HashMap<String, IReinsuranceContractStrategy>();
        for (ParameterHolder parameter : parameters) {
            if (parameter instanceof ParameterObjectParameterHolder) {
                try {
                    IParameterObject parameterHolder = (IParameterObject) parameter.getBusinessObject();
                    for (Class allowedStrategy : contractTypes) {
                        if (parameterHolder.getClass().equals(allowedStrategy)) {
                            contracts.put(parameter.getPath(), (IReinsuranceContractStrategy) parameterHolder);
                        }
                    }
                }
                catch (IllegalArgumentException ex) {
                    // https://issuetracking.intuitive-collaboration.com/jira/browse/PMO-1542
                    LOG.debug("call parameter.getBusinessObject() failed " + ex.toString());
                }
            }
        }
        return contracts;
    }

}

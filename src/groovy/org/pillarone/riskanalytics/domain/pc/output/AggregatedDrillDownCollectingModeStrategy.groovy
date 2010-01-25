package org.pillarone.riskanalytics.domain.pc.output

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.pc.claims.Claim

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregatedDrillDownCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregatedDrillDownCollectingModeStrategy)

    static final String IDENTIFIER = "AGGREGATED_DRILL_DOWN"
    private static final String RESOURCE_BUNDLE = "org.pillarone.riskanalytics.domain.pc.application.applicationResources"
    private static final String PATH_SEPARATOR = ":"
    private String displayName

    PacketCollector packetCollector

    List<SingleValueResult> collect(PacketList packets) {
        return createSingleValueResults(aggregateClaims(packets))
    }

    /**
     * Create a SingleValueResult object for each packetValue.
     * Information about current simulation is gathered from the scopes.
     * The key of the value map is the path.
     */
    private List createSingleValueResults(Map<String, Packet> packets) {
        List singleValueResults = []
        for (Map.Entry<String, Packet> packet in packets) {
            String path = packet.key
            for (Map.Entry field in packet.value.valuesToSave) {
                String fieldName = field.key
                Double value = (Double) field.value
                if (value == Double.NaN || value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY) {
                    int currentPeriod = packetCollector.simulationScope.iterationScope.periodScope.currentPeriod
                    int currentIteration = packetCollector.simulationScope.iterationScope.currentIteration
                    if (LOG.isErrorEnabled()) {
                        LOG.error("${value} collected at ${packetCollector.path} (period ${currentPeriod}) in iteration ${currentIteration} - ignoring")
                    }
                    continue
                }
                SingleValueResult result = new SingleValueResult()
                result.simulationRun = packetCollector.simulationScope.simulationRun
                result.iteration = packetCollector.simulationScope.iterationScope.currentIteration
                result.period = packetCollector.simulationScope.iterationScope.periodScope.currentPeriod
                result.path = getPathMapping(path)
                result.collector = packetCollector.getCollectorMapping()
                result.field = packetCollector.getFieldMapping(fieldName)
                result.valueIndex = 0
                result.value = value

                singleValueResults << result
            }
        }
        return singleValueResults
    }

    /**
     * @return a map with paths as key
     */
    private Map<String, Claim> aggregateClaims(List<Claim> claims) {
        Map resultMap = [:]
        if (claims.empty) {
            return resultMap
        }

        for (claim in claims) {
            String originPath = packetCollector.simulationScope.structureInformation.getPath(claim)
            addToMap(claim, originPath, resultMap)
            String componentPath = packetCollector.path
            if (claim.sender instanceof LobMarker || claim.sender instanceof IReinsuranceContractMarker) {
                String perilPathExtension = claim.peril?.name
                if (perilPathExtension) {
                    addToMap(claim, componentPath, perilPathExtension, resultMap)
                }
            }
            if (claim.sender instanceof LobMarker && claim.reinsuranceContract) {
                String contractPathExtension = claim.reinsuranceContract?.name
                if (contractPathExtension) {
                    addToMap(claim, componentPath, contractPathExtension, resultMap)
                }
            }
            if (claim.sender instanceof IReinsuranceContractMarker && claim.lineOfBusiness) {
                String lobPathExtension = claim.lineOfBusiness?.name
                if (lobPathExtension) {
                    addToMap(claim, componentPath, lobPathExtension, resultMap)
                }
            }
        }
        resultMap
    }

    private void addToMap(Claim claim, String path, Map resultMap) {
        if (resultMap.containsKey(path)) {
            resultMap[path].plus(claim)
        } else {
            Claim clonedClaim = (Claim) claim.copy()
            clonedClaim.claimType = ClaimType.AGGREGATED
            resultMap[path] = clonedClaim
        }
    }

    private void addToMap(Claim claim, String path, String pathExtension, Map resultMap) {
        StringBuilder composedPath = new StringBuilder(path)
        composedPath.append(PATH_SEPARATOR)
        composedPath.append(pathExtension)
        composedPath.append(PATH_SEPARATOR)
        composedPath.append(claim.senderChannelName)
        addToMap(claim, composedPath.toString(), resultMap)
    }

    PathMapping getPathMapping(String extendedPath) {
        PathMapping extendedPathMapping = PathMapping.findByPathName(extendedPath)
        if (!extendedPathMapping) {
            extendedPathMapping = new PathMapping(pathName: extendedPath)
            assert extendedPathMapping.save()
        }
        return extendedPathMapping
    }

    String getDisplayName(Locale locale) {
        if (displayName == null) {
            displayName = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale).getString("ICollectingModeStrategy.${IDENTIFIER}")
        }
        return displayName;
    }

    String getIdentifier() {
        return IDENTIFIER
    }
}

package org.pillarone.riskanalytics.domain.pc.underwriting;


import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoUtilities {
    // todo(sku): use dko if there is a more general solution possible
    public static boolean sameContent(UnderwritingInfo uwInfo1, UnderwritingInfo uwInfo2) {
        return (ExposureInfoUtilities.sameContent(uwInfo1, uwInfo2)
                && uwInfo1.premiumWritten == uwInfo2.premiumWritten
                && uwInfo1.commission == uwInfo2.commission);
    }

    public static void setZero(UnderwritingInfo underwritingInfo) {
        underwritingInfo.premiumWrittenAsIf = 0;
        underwritingInfo.numberOfPolicies = 0;
        underwritingInfo.sumInsured = 0;
        underwritingInfo.maxSumInsured = 0;
        underwritingInfo.premiumWritten = 0;
        underwritingInfo.commission = 0;
    }

    static public UnderwritingInfo aggregate(List<UnderwritingInfo> underwritingInfos) {
        if (underwritingInfos.size() == 0) {
            return null;
        }
        UnderwritingInfo summedUnderwritingInfo = UnderwritingInfoPacketFactory.createPacket();
        for (UnderwritingInfo underwritingInfo: underwritingInfos) {
            summedUnderwritingInfo = summedUnderwritingInfo.plus(underwritingInfo);
            summedUnderwritingInfo.exposureDefinition = underwritingInfo.exposureDefinition;
        }
        return summedUnderwritingInfo;
    }

    static public UnderwritingInfo difference(UnderwritingInfo grossUnderwritingInfo, UnderwritingInfo cededUnderwritingInfo) {
        UnderwritingInfo netUnderwritingInfo = (UnderwritingInfo) grossUnderwritingInfo.copy();
        return netUnderwritingInfo.minus(cededUnderwritingInfo);
    }

    /**
     *  calculates the net underwriting info. Number of policies of gross and ceded have to be equal.
     */
    static public UnderwritingInfo calculateNet(UnderwritingInfo grossUnderwritingInfo, UnderwritingInfo cededUnderwritingInfo) {
        assert grossUnderwritingInfo.numberOfPolicies == cededUnderwritingInfo.numberOfPolicies;
        UnderwritingInfo netUnderwritingInfo = (UnderwritingInfo) grossUnderwritingInfo.copy();
        netUnderwritingInfo.originalUnderwritingInfo = cededUnderwritingInfo.originalUnderwritingInfo;
        netUnderwritingInfo = netUnderwritingInfo.minus(cededUnderwritingInfo);
        netUnderwritingInfo.commission = cededUnderwritingInfo.commission;
        netUnderwritingInfo.numberOfPolicies = grossUnderwritingInfo.numberOfPolicies;
        return netUnderwritingInfo;
    }

    static public void difference(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo, List<UnderwritingInfo> difference) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        assert difference != null;
        assert difference.size() == 0;
        for (int i = 0; i < minuendUwInfo.size(); i++) {
            difference.add(minuendUwInfo.get(i).minus(subtrahendUwInfo.get(i)));
        }
    }

    static public List<UnderwritingInfo> difference(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        List<UnderwritingInfo> aggregateUnderwritingInfo = new ArrayList<UnderwritingInfo>(minuendUwInfo.size());
        difference(minuendUwInfo, subtrahendUwInfo, aggregateUnderwritingInfo);
        return aggregateUnderwritingInfo;
    }

/*    static public List<UnderwritingInfo> calculateNet(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo) {
        assert minuendUwInfo.size() >= subtrahendUwInfo.size();
        List<UnderwritingInfo> aggregateUnderwritingInfo = new ArrayList<UnderwritingInfo>(minuendUwInfo.size());
        for (int i = 0; i < minuendUwInfo.size(); i++) {
            UnderwritingInfo grossUnderwritingInfo = minuendUwInfo.get(i);
            UnderwritingInfo cededUnderwritingInfo = findUnderwritingInfo(subtrahendUwInfo, minuendUwInfo.get(i).id)
            if (cededUnderwritingInfo) {
                aggregateUnderwritingInfo << calculateNet(grossUnderwritingInfo, cededUnderwritingInfo)
            }
            else {
                aggregateUnderwritingInfo << new UnderwritingInfo(grossUnderwritingInfo)
            }
        }
        return aggregateUnderwritingInfo
    }
*/
    static public List<UnderwritingInfo> calculateNet(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        List<UnderwritingInfo> difference = new ArrayList<UnderwritingInfo>(minuendUwInfo.size());
        for (UnderwritingInfo grossUnderwritingInfo : minuendUwInfo) {
            UnderwritingInfo cededUnderwritingInfo = findUnderwritingInfo(subtrahendUwInfo, grossUnderwritingInfo);
            if (cededUnderwritingInfo != null) {
                difference.add(calculateNet(grossUnderwritingInfo, cededUnderwritingInfo));
            }
            else {
                UnderwritingInfo netUnderwritingInfo = (UnderwritingInfo) grossUnderwritingInfo.copy();
                netUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;
                difference.add(netUnderwritingInfo);
            }
        }
        return difference;
    }



   static public void calculateNet(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo, List<UnderwritingInfo> difference) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        for (int i = 0; i < minuendUwInfo.size(); i++) {
            UnderwritingInfo grossUnderwritingInfo = minuendUwInfo.get(i);
            UnderwritingInfo cededUnderwritingInfo = findUnderwritingInfo(subtrahendUwInfo, grossUnderwritingInfo);
            if (cededUnderwritingInfo != null) {
                difference.add(calculateNet(grossUnderwritingInfo, cededUnderwritingInfo));
            }
            else {
                UnderwritingInfo netUnderwritingInfo = (UnderwritingInfo) grossUnderwritingInfo.copy();
                netUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;
                difference.add(netUnderwritingInfo);
            }
        }
    }

    static public UnderwritingInfo findUnderwritingInfo(List<UnderwritingInfo> underwritingInfos, UnderwritingInfo refUwInfo) {
        for(UnderwritingInfo underwritingInfo : underwritingInfos) {
            if (underwritingInfo.originalUnderwritingInfo.equals(refUwInfo) || underwritingInfo.originalUnderwritingInfo.equals(refUwInfo.originalUnderwritingInfo)) {
                return underwritingInfo;
            }
        }
        return null;
    }

    /**
     * @param underwritingInfos the list of underwritingInfo packets to filter
     * @param contracts the contract markers to filter by, if any; null means no filtering (all are accepted)
     * @param acceptedUnderwritingInfo the list of underwritingInfo packets whose contract is listed in contracts
     * @param rejectedUnderwritingInfo (if not null) the remaining underwritingInfo packets that were filtered out
     */
    public static void segregateUnderwritingInfoByContract(List<UnderwritingInfo> underwritingInfos, List<IReinsuranceContractMarker> contracts,
                                                           List<UnderwritingInfo> acceptedUnderwritingInfo,
                                                           List<UnderwritingInfo> rejectedUnderwritingInfo) {
        if (contracts == null || contracts.size() == 0) {
            acceptedUnderwritingInfo.addAll(underwritingInfos);
        }
        else {
            for (UnderwritingInfo underwritingInfo : underwritingInfos) {
                if (contracts.contains(underwritingInfo.getReinsuranceContract())) {
                    acceptedUnderwritingInfo.add(underwritingInfo);
                }
                else if (rejectedUnderwritingInfo != null) {
                    rejectedUnderwritingInfo.add(underwritingInfo);
                }
            }
        }
    }
}

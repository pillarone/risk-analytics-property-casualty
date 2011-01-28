package org.pillarone.riskanalytics.domain.pc.underwriting;


import org.pillarone.riskanalytics.domain.pc.lob.LobMarker;
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.PacketUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoUtilities {

    public static boolean sameContent(UnderwritingInfo uwInfo1, UnderwritingInfo uwInfo2) {
        return (PacketUtilities.sameContent(uwInfo1, uwInfo2)
                && uwInfo1.getNumberOfPolicies() == uwInfo2.getNumberOfPolicies()
                && uwInfo1.getSumInsured() == uwInfo2.getSumInsured()
                && uwInfo1.getMaxSumInsured() == uwInfo2.getMaxSumInsured()
                && PacketUtilities.equals(uwInfo2.getExposureDefinition(), uwInfo2.getExposureDefinition())
                && uwInfo1.getPremium() == uwInfo2.getPremium())
                && uwInfo1.getCommission() == uwInfo2.getCommission();
    }

    public static void setZero(UnderwritingInfo underwritingInfo) {
        underwritingInfo.setNumberOfPolicies(0);
        underwritingInfo.setSumInsured(0);
        underwritingInfo.setMaxSumInsured(0);
        underwritingInfo.setPremium(0);
        underwritingInfo.setCommission(0);
    }

    static public UnderwritingInfo aggregate(List<UnderwritingInfo> underwritingInfos) {
        if (underwritingInfos.size() == 0) {
            return null;
        }
        UnderwritingInfo summedUnderwritingInfo;
        if (underwritingInfos.get(0) instanceof CededUnderwritingInfo)
            summedUnderwritingInfo = CededUnderwritingInfoPacketFactory.createPacket();
        else
            summedUnderwritingInfo = UnderwritingInfoPacketFactory.createPacket();
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            summedUnderwritingInfo.plus(underwritingInfo);
            summedUnderwritingInfo.setExposureDefinition(underwritingInfo.getExposureDefinition());
        }
        return correctMetaProperties(summedUnderwritingInfo, underwritingInfos);
    }

    static public UnderwritingInfo correctMetaProperties(UnderwritingInfo result, List<UnderwritingInfo> underwritingInfos) {
        UnderwritingInfo verifiedResult = underwritingInfos.get(0).copy();
        verifiedResult.scale(0);
        verifiedResult.plus(result);
        LobMarker lob = verifiedResult.getLineOfBusiness();
        IReinsuranceContractMarker reinsuranceContract = verifiedResult.getReinsuranceContract();
        boolean underwritingInfosOfDifferentLobs = lob == null;
        boolean underwritingInfosOfDifferentContracts = reinsuranceContract == null;
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            if (!underwritingInfosOfDifferentLobs && !lob.equals(underwritingInfo.getLineOfBusiness())) {
                underwritingInfosOfDifferentLobs = true;
            }
            if (!underwritingInfosOfDifferentContracts && !reinsuranceContract.equals(underwritingInfo.getReinsuranceContract())) {
                underwritingInfosOfDifferentContracts = true;
            }
        }
        if (underwritingInfosOfDifferentLobs) {
            verifiedResult.setLineOfBusiness(null);
        }
        if (underwritingInfosOfDifferentContracts) {
            verifiedResult.setReinsuranceContract(null);
        }
        return verifiedResult;
    }

    static public UnderwritingInfo difference(UnderwritingInfo grossUnderwritingInfo, UnderwritingInfo cededUnderwritingInfo) {
        UnderwritingInfo netUnderwritingInfo = grossUnderwritingInfo.copy();
        netUnderwritingInfo.minus(cededUnderwritingInfo);
        return netUnderwritingInfo;
    }

    /**
     * calculates the net underwriting info. Number of policies of gross and ceded have to be equal.
     * caveat: We have to be careful with the number of policies here used for deriving the (averaged)
     * sum insured from the total sum insured.
     * For the correct approach follow the two outlined steps:
     * step 1: Compute the difference of the total sums insured (analogously to premiumWrittenAsIf)
     * step 2: To obtain the averaged sum insured use the number of policies of the minuend only.
     * This is the natural procedure for insurance contracts where the minuend corresponds to the gross portfolio
     * while the subtrahend is associated to the ceded portfolio. Hence for the number of policies we solely use the
     * information from the gross portfolio.
     * Sole exception: gross and ceded portfolio are equal, then numberOfPolicies =0.
     */
    // todo (jwa): assert in java has to be enabled explicitly; in my opinion it should not be used here for the number of policies
    static public UnderwritingInfo calculateNet(UnderwritingInfo grossUnderwritingInfo, UnderwritingInfo cededUnderwritingInfo) {
        assert grossUnderwritingInfo.getNumberOfPolicies() == cededUnderwritingInfo.getNumberOfPolicies();
        UnderwritingInfo netUnderwritingInfo = grossUnderwritingInfo.copy();
        netUnderwritingInfo.setOriginalUnderwritingInfo(cededUnderwritingInfo.getOriginalUnderwritingInfo());
        netUnderwritingInfo.minus(cededUnderwritingInfo);
        netUnderwritingInfo.setNumberOfPolicies(grossUnderwritingInfo.getNumberOfPolicies());
        netUnderwritingInfo.setSumInsured(grossUnderwritingInfo.getSumInsured() * grossUnderwritingInfo.getNumberOfPolicies() - cededUnderwritingInfo.getSumInsured() * cededUnderwritingInfo.getNumberOfPolicies());
        if (netUnderwritingInfo.getNumberOfPolicies() > 0) {
            netUnderwritingInfo.setSumInsured(netUnderwritingInfo.getSumInsured() / netUnderwritingInfo.getNumberOfPolicies());
        }
        else {
            netUnderwritingInfo.setSumInsured(grossUnderwritingInfo.getSumInsured() - cededUnderwritingInfo.getSumInsured());
        }
        if (netUnderwritingInfo.getPremium() == 0 && netUnderwritingInfo.getCommission() == 0 && netUnderwritingInfo.getSumInsured() == 0) {
            netUnderwritingInfo.setNumberOfPolicies(0);
        }
        return netUnderwritingInfo;
    }

    static public void difference(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo, List<UnderwritingInfo> difference) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        assert difference != null;
        assert difference.size() == 0;
        for (int i = 0; i < minuendUwInfo.size(); i++) {
            minuendUwInfo.get(i).minus(subtrahendUwInfo.get(i));
            difference.add(minuendUwInfo.get(i));
        }
    }

    static public List<UnderwritingInfo> difference(List<UnderwritingInfo> minuendUwInfo, List<UnderwritingInfo> subtrahendUwInfo) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        List<UnderwritingInfo> difference = new ArrayList<UnderwritingInfo>(minuendUwInfo.size());
        difference(minuendUwInfo, subtrahendUwInfo, difference);
        return difference;
    }

    static public List<UnderwritingInfo> calculateNet(List<UnderwritingInfo> minuendUwInfo, List<CededUnderwritingInfo> subtrahendUwInfo) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        List<UnderwritingInfo> difference = new ArrayList<UnderwritingInfo>(minuendUwInfo.size());
        for (UnderwritingInfo grossUnderwritingInfo : minuendUwInfo) {
            CededUnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoUtilities.findUnderwritingInfo(subtrahendUwInfo, grossUnderwritingInfo);
            if (cededUnderwritingInfo != null) {
                difference.add(calculateNet(grossUnderwritingInfo, cededUnderwritingInfo));
            }
            else {
                UnderwritingInfo netUnderwritingInfo = grossUnderwritingInfo.copy();
                netUnderwritingInfo.setOriginalUnderwritingInfo(grossUnderwritingInfo);
                difference.add(netUnderwritingInfo);
            }
        }
        return difference;
    }


    static public void calculateNet(List<UnderwritingInfo> minuendUwInfo, List<CededUnderwritingInfo> subtrahendUwInfo, List<UnderwritingInfo> difference) {
        assert minuendUwInfo.size() == subtrahendUwInfo.size();
        for (int i = 0; i < minuendUwInfo.size(); i++) {
            UnderwritingInfo grossUnderwritingInfo = minuendUwInfo.get(i);
            UnderwritingInfo cededUnderwritingInfo = CededUnderwritingInfoUtilities.findUnderwritingInfo(subtrahendUwInfo, grossUnderwritingInfo);
            if (cededUnderwritingInfo != null) {
                difference.add(calculateNet(grossUnderwritingInfo, cededUnderwritingInfo));
            }
            else {
                UnderwritingInfo netUnderwritingInfo = grossUnderwritingInfo.copy();
                netUnderwritingInfo.setOriginalUnderwritingInfo(grossUnderwritingInfo);
                difference.add(netUnderwritingInfo);
            }
        }
    }

    static public List<UnderwritingInfo> setCommissionZero(List<UnderwritingInfo> UnderwritingInfoGross) {
        List<UnderwritingInfo> UnderwritingInfoGrossWithZeroCommission = new ArrayList<UnderwritingInfo>(UnderwritingInfoGross.size());
        for (UnderwritingInfo grossUnderwritingInfo : UnderwritingInfoGross) {
            UnderwritingInfo grossUnderwritingInfoWithZeroCommission = grossUnderwritingInfo.copy();
            grossUnderwritingInfoWithZeroCommission.setCommission(0);
            UnderwritingInfoGrossWithZeroCommission.add(grossUnderwritingInfoWithZeroCommission);
        }
        return UnderwritingInfoGrossWithZeroCommission;
    }

    static public UnderwritingInfo findUnderwritingInfo(List<UnderwritingInfo> underwritingInfos, UnderwritingInfo refUwInfo) {
        for (UnderwritingInfo underwritingInfo : underwritingInfos) {
            if (underwritingInfo.getOriginalUnderwritingInfo().equals(refUwInfo)
                    || underwritingInfo.getOriginalUnderwritingInfo().equals(refUwInfo.getOriginalUnderwritingInfo())) {
                return underwritingInfo;
            }
        }
        return null;
    }


}

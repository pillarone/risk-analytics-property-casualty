package org.pillarone.riskanalytics.domain.pc.reinsurance.contracts

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurerDefault
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.pc.reinsurance.IReinsurerMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.claims.ClaimFilterUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingFilterUtilities
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoUtilities
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfoPacketFactory
import org.pillarone.riskanalytics.domain.pc.filter.FilterUtils

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MultiLineReinsuranceContractWithDefault extends MultiLineReinsuranceContract {

    PacketList<ReinsurerDefault> inReinsurersDefault = new PacketList(ReinsurerDefault)

    ComboBoxTableMultiDimensionalParameter parmCoveredPerils = new ComboBoxTableMultiDimensionalParameter([''], ['perils'], IPerilMarker)
    ConstrainedString parmReinsurer = new ConstrainedString(IReinsurerMarker, '')

    boolean defaultOccurred = false;

    public void doCalculation() {
        defaultOccurred = defaultOccurred()
        super.doCalculation();
    }

    public void filterInChannel(PacketList inChannel, PacketList source) {
        if (inChannel.equals(inClaims)) {
            inChannel.addAll(ClaimFilterUtilities.filterClaimsByPerilAndLob(source,
                    FilterUtils.getCoveredPerils(parmCoveredPerils, periodStore),
                    FilterUtils.getCoveredLines(parmCoveredLines, periodStore)))
            if (inChannel.isEmpty()) {
                inChannel.addAll(UnderwritingFilterUtilities.filterUnderwritingInfoByLob(source,
                    FilterUtils.getCoveredLines(parmCoveredPerils, inChannel, periodStore)));
            }
        }
        else {
            super.filterInChannel(inChannel, source)
        }
    }

    protected Claim getCoveredClaim(Claim claim, Component origin) {
        if (defaultOccurred) {
            Claim claimCeded = claim.copy();
            claimCeded.origin = origin;
            claimCeded.value = 0d;
            claimCeded.setOriginalClaim(claim);
            return claimCeded;
        }
        else {
            return super.getCoveredClaim(claim, origin);
        }
    }

    protected void calculateUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                              List<UnderwritingInfo> cededUnderwritingInfos,
                                              List<UnderwritingInfo> netUnderwritingInfos,
                                              List<Claim> cededClaims) {
        parmContractStrategy.initCededPremiumAllocation(cededClaims, grossUnderwritingInfos);
        if (defaultOccurred) {
            for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
                UnderwritingInfo cededUnderwritingInfo = getCededUnderwritingInfoZero(underwritingInfo);
                setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
                cededUnderwritingInfos.add(cededUnderwritingInfo);
                UnderwritingInfo netUnderwritingInfo = UnderwritingInfoUtilities.calculateNet(underwritingInfo, cededUnderwritingInfo);
                setOriginalUnderwritingInfo(underwritingInfo, netUnderwritingInfo);
                netUnderwritingInfos.add(netUnderwritingInfo);
            }
        }
        else {
            super.calculateUnderwritingInfos(grossUnderwritingInfos, cededUnderwritingInfos, netUnderwritingInfos)
        }

    }

    protected void calculateCededUnderwritingInfos(List<UnderwritingInfo> grossUnderwritingInfos,
                                                   List<UnderwritingInfo> cededUnderwritingInfos,
                                                   List<Claim> cededClaims) {
        parmContractStrategy.initCededPremiumAllocation(cededClaims, grossUnderwritingInfos);
        if (defaultOccurred) {
            for (UnderwritingInfo underwritingInfo: grossUnderwritingInfos) {
                UnderwritingInfo cededUnderwritingInfo = getCededUnderwritingInfoZero(underwritingInfo);
                setOriginalUnderwritingInfo(underwritingInfo, cededUnderwritingInfo);
                cededUnderwritingInfos.add(cededUnderwritingInfo);
            }
        }
        else {
            super.calculateCededUnderwritingInfos(grossUnderwritingInfos, cededUnderwritingInfos, cededClaims);
        }
    }

    private UnderwritingInfo getCededUnderwritingInfoZero(UnderwritingInfo grossUnderwritingInfo) {
        UnderwritingInfo cededUnderwritingInfo = UnderwritingInfoPacketFactory.copy(grossUnderwritingInfo);
        if ((grossUnderwritingInfo != null) && (grossUnderwritingInfo.originalUnderwritingInfo != null)) {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo.originalUnderwritingInfo;
        }
        else {
            cededUnderwritingInfo.originalUnderwritingInfo = grossUnderwritingInfo;
        }
        cededUnderwritingInfo.premium = 0;
        cededUnderwritingInfo.premiumWrittenAsIf = 0;
        cededUnderwritingInfo.sumInsured = 0;
        cededUnderwritingInfo.maxSumInsured = 0;
        cededUnderwritingInfo.commission = 0;
        return cededUnderwritingInfo;
    }

    private boolean defaultOccurred() {
        for (ReinsurerDefault reinsurerDefault: inReinsurersDefault) {
            if (reinsurerDefault.reinsurer.equals(parmReinsurer)) {
                return reinsurerDefault.defaultOccurred;
            }
        }
        return false;
    }
}
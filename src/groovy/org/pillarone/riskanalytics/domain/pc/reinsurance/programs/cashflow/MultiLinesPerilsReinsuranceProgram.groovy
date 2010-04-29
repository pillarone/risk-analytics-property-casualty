package org.pillarone.riskanalytics.domain.pc.reinsurance.programs.cashflow

import org.pillarone.riskanalytics.core.wiring.WireCategory as WC
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.MarketClaimsMerger
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.cashflow.MultiLinesPerilsReinsuranceContractComparator
import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.MarketUnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class MultiLinesPerilsReinsuranceProgram extends DynamicComposedComponent {

    MultiLinesPerilsReinsuranceContract createDefaultSubComponent() {
        return new MultiLinesPerilsReinsuranceContract()
    }

    protected void wireContractInClaims(MultiLinesPerilsReinsuranceContract contract, MarketClaimsMerger claimsMerger) {
        if (((MultiLinesPerilsReinsuranceContract) contract).parmBasedOn.equals(ReinsuranceContractBase.NET)) {
            doWire WC, contract, 'inClaims', claimsMerger, 'outClaimsNet'
        }
        else if (contract.parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
            doWire WC, contract, 'inClaims', claimsMerger, 'outClaimsCeded'
        }
    }

    /* following code is copied from DRP */

    PacketList<Claim> inClaims = new PacketList(Claim)

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentNet = new PacketList(ClaimDevelopmentPacket)
    PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentGross = new PacketList(ClaimDevelopmentPacket)
    PacketList<ClaimDevelopmentPacket> outClaimsDevelopmentCeded = new PacketList(ClaimDevelopmentPacket)

    PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outCoverUnderwritingInfo = new PacketList(UnderwritingInfo)

    protected List sortedIndizes
    private List<Component> contractsSorted
    private int numberOfContracts
    private int numberOfPriorities

    List<MarketClaimsMerger> claimsMergers = new ArrayList<MarketClaimsMerger>()
    List<MarketUnderwritingInfoMerger> underwritingInfoMergers = new ArrayList<MarketUnderwritingInfoMerger>()

    /**
     *  A ClaimsMerger is used for every inuring priority.
     *  Each ClaimsMerger receives the net claims of the preceeding ClaimsMerger and the ceded claims of the contracts
     *  with the same inuring priority. The merged net claims are sent to the contracts with the next higher inuring
     *  priority and the following ClaimsMerger. The merged ceded claims are sent to the last ClaimsMerger.
     *  The first ClaimsMerger receives the inClaims of the program.
     *  The last ClaimsMerger prepares the claims for the out channels. It receives the inClaims of the program, the
     *  ceded claims of all preceeding ClaimsMerger and the contracts with the highest inuring priority. But the net
     *  claims of the second last contract are not sent to it.
     */
    public void wire() {
        if (componentList.size() > 0) {
            initWiring()
            wireContractsClaimsChannels()
            wireClaimsMergers()

            // handle underwriting information if the in channel and at least one out underwriting info channel is connected
            if (isReceiverWired(inUnderwritingInfo)) {
                wireContractsUnderwritingChannels()
                wireUnderwritingInfoMergers()
            }
        }
        wireReplicatingOutChannels()
    }

    /**
     *  Sets contractsSorted and sortedIndizes according the inuring priority of the contracts.
     *  Sets numberOfContracts and numberOfPriorities
     */
    private void initWiring() {
        MultiLinesPerilsReinsuranceContractComparator comparator = MultiLinesPerilsReinsuranceContractComparator.getInstance()
        contractsSorted = new ArrayList<Component>(componentList)

        Collections.sort(contractsSorted, comparator)
        sortedIndizes = contractsSorted.collect {componentList.indexOf(it)}

        numberOfContracts = componentList.size()
        numberOfPriorities = 1
        for (int i = 0; i < numberOfContracts - 1; i++) {
            if (getContract(i + 1).parmInuringPriority > getContract(i).parmInuringPriority) {
                numberOfPriorities++
            }
        }
    }

    private void wireContractsClaimsChannels() {
        int currentPriority = -1
        int currentMerger = -1
        for (int i = 0; i < numberOfContracts; i++) {
            if (getContract(i).parmInuringPriority > currentPriority) { // add a new merger
                currentMerger++
                claimsMergers << new MarketClaimsMerger()
                currentPriority = getContract(i).parmInuringPriority
                claimsMergers[-1].name = "inuring priority ${currentPriority}"
            }
            if (currentMerger == 0) {
                doWire PRC, getContract(i), 'inClaims', this, 'inClaims'
            }
            else if (currentMerger > 0) {
                wireContractInClaims(getContract(i), (MarketClaimsMerger) claimsMergers[currentMerger - 1])
            }
            doWire WC, claimsMergers[currentMerger], 'inClaimsCeded', getContract(i), 'outCoveredClaims'
        }
    }

    private void wireContractsUnderwritingChannels() {
        int currentPriority = -1
        int currentMerger = -1
        for (int i = 0; i < numberOfContracts; i++) {
            if (getContract(i).parmInuringPriority > currentPriority) { // add a new merger
                currentMerger++
                underwritingInfoMergers << new MarketUnderwritingInfoMerger()
                currentPriority = getContract(i).parmInuringPriority
                underwritingInfoMergers[-1].name = "inuring priority ${currentPriority}"
            }
            if (currentMerger == 0) {
                doWire PRC, getContract(i), 'inUnderwritingInfo', this, 'inUnderwritingInfo'
            }
            else if (currentMerger > 0) {
                wireContractInUnderwritingInfo(getContract(i), (MarketUnderwritingInfoMerger) underwritingInfoMergers[currentMerger - 1])
            }
            doWire WC, underwritingInfoMergers[currentMerger], 'inUnderwritingInfoCeded', getContract(i), 'outCoverUnderwritingInfo'
        }
    }

    protected void wireContractInUnderwritingInfo(MultiLinesPerilsReinsuranceContract contract, MarketUnderwritingInfoMerger uwInfoMerger) {
        doWire WC, contract, 'inUnderwritingInfo', uwInfoMerger, 'outUnderwritingInfoNet'
    }

    /**
     *
     */
    private void wireReplicatingOutChannels() {
        if (componentList.size() > 0) {
            doWire PRC, this, 'outClaimsCeded', claimsMergers[-1], 'outClaimsCeded'
            doWire PRC, this, 'outClaimsGross', claimsMergers[-1], 'outClaimsGross'
            doWire PRC, this, 'outClaimsNet', claimsMergers[-1], 'outClaimsNet'
            doWire PRC, this, 'outClaimsDevelopmentGross', claimsMergers[-1], 'outClaimsDevelopmentGross'
            doWire PRC, this, 'outClaimsDevelopmentCeded', claimsMergers[-1], 'outClaimsDevelopmentCeded'
            doWire PRC, this, 'outClaimsDevelopmentNet', claimsMergers[-1], 'outClaimsDevelopmentNet'
            if (isReceiverWired(inUnderwritingInfo)) {
                doWire PRC, this, 'outCoverUnderwritingInfo', underwritingInfoMergers[-1], 'outUnderwritingInfoCeded'
                doWire PRC, this, 'outNetAfterCoverUnderwritingInfo', underwritingInfoMergers[-1], 'outUnderwritingInfoNet'
                doWire PRC, this, 'outUnderwritingInfo', underwritingInfoMergers[-1], 'outUnderwritingInfoGross'
            }
        }
        else { // program has no contracts
            doWire WC, this, 'outClaimsGross', this, 'inClaims'
            doWire WC, this, 'outUnderwritingInfo', this, 'inUnderwritingInfo'
        }
    }



    private void wireClaimsMergers() {
        for (int i = 1; i < numberOfPriorities; i++) {
            doWire WC, claimsMergers[i], 'inClaimsCeded', claimsMergers[i - 1], 'outClaimsCeded'
        }
        for (int i = 0; i < numberOfPriorities; i++) {
            doWire PRC, claimsMergers[i], 'inClaimsGross', this, 'inClaims'
        }
    }

    private void wireUnderwritingInfoMergers() {
        for (int i = 0; i < numberOfPriorities; i++) {
            doWire PRC, underwritingInfoMergers[i], 'inUnderwritingInfoGross', this, 'inUnderwritingInfo'
        }
        for (int i = 1; i < numberOfPriorities; i++) {
            doWire WC, underwritingInfoMergers[i], 'inUnderwritingInfoCeded', underwritingInfoMergers[i - 1], 'outUnderwritingInfoCeded'
        }
    }

    public MultiLinesPerilsReinsuranceContract getContract(int index) {
        componentList[sortedIndizes[index]]
    }

    /**
     * Helper method for wiring when sender or receiver are determined dynamically
     */
    public static void doWire(category, receiver, inChannelName, sender, outChannelName) {
        category.doSetProperty(receiver, inChannelName, category.doGetProperty(sender, outChannelName))
    }

    public String getGenericSubComponentName() {
        return "contracts"
    }
}

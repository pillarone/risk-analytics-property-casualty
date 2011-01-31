package org.pillarone.riskanalytics.domain.pc.reinsurance.programs

import org.pillarone.riskanalytics.core.wiring.WireCategory as WC
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.claims.Claim
import org.pillarone.riskanalytics.domain.pc.claims.MarketClaimsMerger
import org.pillarone.riskanalytics.domain.pc.constants.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.MultiCoverAttributeReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractComparator
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.reserves.fasttrack.ClaimDevelopmentLeanPacket
import org.pillarone.riskanalytics.domain.pc.underwriting.MarketUnderwritingInfoMerger
import org.pillarone.riskanalytics.domain.pc.underwriting.UnderwritingInfo
import org.pillarone.riskanalytics.domain.pc.underwriting.CededUnderwritingInfo

/**
 * A DynamicReinsuranceProgram is a DynamicComposedComponent -- i.e. a container of a sequence of
 * similar components -- which, in this case, are reinsurance contracts.
 *
 * The contracts are wired in series in the order specified by their inuring priorities (with tying
 * contracts wired in parallel), and merged after each stage to calculate the net from that stage.
 *
 * DynamicReinsuranceProgram uses the following input and output channels:
 *     in channels:
 *         inClaims
 *         inUnderwritingInfo
 *     out channels:
 *         outClaims* (* = Net, Gross, Ceded)
 *         outClaimsDevelopmentLean* (* = Net, Gross, Ceded)
 *         out*UnderwritingInfo (* = Cover, NetAfterCover, -)
 * Ceded/covered packets from all contained reinsurance contracts as well as final net/uncovered
 * amounts are sent out of the DynamicReinsuranceProgram. Claims are always processed, however
 * underwriting info amounts are only calculated when a relevant in and out channel is wired.
 *
 * A typical object starts its life, get some ReinsuranceContract objects added to it with the
 * addSubComponent() method, gets wired with wire(), and then may be invoked with start() or as the
 * result of an upstream start() or doCalculation() once all in channels are ready.
 * A helper method, createDefaultSubComponent(), returns a TRIVIAL contract with inuring priority 0
 * which could be customized and added to the program with addSubComponent, but its use is not required.
 *
 * This class uses Market*Merger components (i.e. MarketClaimsMerger & MarketUnderwritingInfoMerger),
 * as noted above, to accomplish the merging after each stage (i.e. each distinct inuring priority >=0).
 * The class will throw an error during wiring if any inuring priority is <0. //todo(bgi): add test case
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class DynamicReinsuranceProgram extends DynamicComposedComponent {

    PacketList<Claim> inClaims = new PacketList(Claim)

    PacketList<UnderwritingInfo> inUnderwritingInfo = new PacketList(UnderwritingInfo)

    PacketList<Claim> outClaimsNet = new PacketList(Claim)
    PacketList<Claim> outClaimsGross = new PacketList(Claim)
    PacketList<Claim> outClaimsCeded = new PacketList(Claim)

    // todo(sku): remove the following and related lines as soon as PMO-648 is resolved
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanNet = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanGross = new PacketList(ClaimDevelopmentLeanPacket)
    PacketList<ClaimDevelopmentLeanPacket> outClaimsDevelopmentLeanCeded = new PacketList(ClaimDevelopmentLeanPacket)

    PacketList<UnderwritingInfo> outNetAfterCoverUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<UnderwritingInfo> outUnderwritingInfo = new PacketList(UnderwritingInfo)
    PacketList<CededUnderwritingInfo> outCoverUnderwritingInfo = new PacketList(CededUnderwritingInfo)

    protected List sortedIndizes
    private List<Component> contractsSorted
    private int numberOfContracts
    private int numberOfPriorities

    List<MarketClaimsMerger> claimsMergers = new ArrayList<MarketClaimsMerger>()
    List<MarketUnderwritingInfoMerger> underwritingInfoMergers = new ArrayList<MarketUnderwritingInfoMerger>()

    private static Log LOG = LogFactory.getLog(DynamicReinsuranceProgram.class);

    public ReinsuranceContract createDefaultSubComponent() {
        ReinsuranceContract newContract = new ReinsuranceContract(parmInuringPriority: 0,
                parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]))
        return newContract
    }

    /**
     *  A ClaimsMerger is used for every inuring priority.
     *  Each ClaimsMerger receives the net claims of the preceding ClaimsMerger and the ceded claims of the contracts
     *  with the same inuring priority. The merged net claims are sent to the contracts with the next higher inuring
     *  priority and the following ClaimsMerger. The merged ceded claims are sent to the last ClaimsMerger.
     *  The first ClaimsMerger receives the inClaims of the program.
     *  The last ClaimsMerger prepares the claims for the out channels. It receives the inClaims of the program, the
     *  ceded claims of all preceding ClaimsMergers and the contracts with the highest inuring priority. But the net
     *  claims of the second last contract are not sent to it.
     */
    public void wire() {
        if (subComponentCount() > 0) {
            initWiring()
            wireContractsClaimsChannels()
            wireClaimsMergers()

            // handle underwriting information if the in channel and at least one out underwriting info channel is connected
            if (isReceiverWired(inUnderwritingInfo)) {
                wireContractsUnderwritingChannels()
                wireUnderwritingInfoMergers()
            }
            wireReplicatingOutChannels()
        }
        else { // program has no contracts
            doWire WC, this, 'outClaimsGross', this, 'inClaims'
            doWire WC, this, 'outUnderwritingInfo', this, 'inUnderwritingInfo'
        }
    }

    /**
     *  Sets contractsSorted and sortedIndizes according the inuring priority of the contracts.
     *  Sets numberOfContracts and numberOfPriorities
     */
    private void initWiring() {
        ReinsuranceContractComparator comparator = ReinsuranceContractComparator.getInstance()
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
                if (getContract(i) instanceof MultiCoverAttributeReinsuranceContract &&
                        ((MultiCoverAttributeReinsuranceContract) getContract(i)).parmBasedOn.equals(ReinsuranceContractBase.CEDED)) {
                    //todo(bgi): warn the user that the parameter makes no sense and will be ignored for this run
                    if (LOG.isDebugEnabled()) {
                        String contractName = getContract(i).normalizedName + " with " + claimsMergers[-1].name
                        LOG.debug("DynamicReinsuranceProgram contract claims wiring: ignoring contract base parameter value CEDED, using NET, for contract $contractName")
                    }
                }
            }
            else if (currentMerger > 0) {
                wireContractInClaims(getContract(i), (MarketClaimsMerger) claimsMergers[currentMerger - 1])
            }
            doWire WC, claimsMergers[currentMerger], 'inClaimsCeded', getContract(i), 'outCoveredClaims'
            doWire PRC, this, 'outClaimsCeded', getContract(i), 'outCoveredClaims'
            doWire PRC, this, 'outClaimsDevelopmentLeanCeded', getContract(i), 'outClaimsDevelopmentLeanCeded'
        }
    }

    protected void wireContractInClaims(ReinsuranceContract contract, MarketClaimsMerger claimsMerger) {
        doWire WC, contract, 'inClaims', claimsMerger, 'outClaimsNet'
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
            doWire PRC, this, 'outCoverUnderwritingInfo', getContract(i), 'outCoverUnderwritingInfo'
        }
    }

    protected void wireContractInUnderwritingInfo(ReinsuranceContract contract, MarketUnderwritingInfoMerger uwInfoMerger) {
        doWire WC, contract, 'inUnderwritingInfo', uwInfoMerger, 'outUnderwritingInfoNet'
    }

    /**
     *
     */
    private void wireReplicatingOutChannels() {
        doWire PRC, this, 'outClaimsGross', claimsMergers[-1], 'outClaimsGross'
        doWire PRC, this, 'outClaimsNet', claimsMergers[-1], 'outClaimsNet'
        doWire PRC, this, 'outClaimsDevelopmentLeanGross', claimsMergers[-1], 'outClaimsDevelopmentLeanGross'
        doWire PRC, this, 'outClaimsDevelopmentLeanNet', claimsMergers[-1], 'outClaimsDevelopmentLeanNet'
        if (isReceiverWired(inUnderwritingInfo)) {
            doWire PRC, this, 'outNetAfterCoverUnderwritingInfo', underwritingInfoMergers[-1], 'outUnderwritingInfoNet'
            doWire PRC, this, 'outUnderwritingInfo', underwritingInfoMergers[-1], 'outUnderwritingInfoGross'
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

    public ReinsuranceContract getContract(int index) {
        componentList[sortedIndizes[index]]
    }

    public String getGenericSubComponentName() {
        return "contracts"
    }

}
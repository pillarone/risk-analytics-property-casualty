package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author shartmann (at) munichre (dot) com
 */
class ClaimFilterUtilitiesTests extends GroovyTestCase {

    List<ClaimDevelopmentPacket> claim
    List<TestPerilComponent> peril
    Map<String, TestLobComponent> lob // also used for reserve markers
    List<IReinsuranceContractMarker> contract

    void setUp() {

        // create some fake LOBs, perils and contracts
        List<String> letters = ['A', 'B', 'C']
        lob = new HashMap<String, TestLobComponent>(letters.size())
        letters.each { lob.put(it, new TestLobComponent(name: "lob ${it}")) } // lob['a'] is "lob A" (normalized name "lob a")
        peril = [1, 2, 3].collect {new TestPerilComponent(name: "Peril ${it}")} // peril[0] is "Peril 1" (normalized name "peril 1")
        contract = [1, 2].collect {new TestContractComponent(name: "Contract ${it}")} // note: we pass the normalized name to the filter; e.g. "contract 1" (with lowercase c)

        // create some claim packets, and map them each to a peril and a contract...
        // every second (resp. third) claim is mapped to the same contract (resp. peril)
        // only claims 1-5 & claim 7 have a LOB (3 from peril 1, 2 from peril 2, 1 from peril 3; see below)
        claim = (1..12).asList().collect {
            ClaimDevelopmentPacket claim = new ClaimDevelopmentPacket(
                    incurred: it*100,
                    paid: it*50,
                    senderChannelName: "Claim ${it}",           // claim    1, 2, 3, 4, 5, 6, 7...12
                    // lineOfBusiness is set below              // lob      A, A, A, B, A, none, C (claims>7: none)
            )
            claim.addMarker(IPerilMarker, peril[(((int) it) - 1) % 3])  // peril    1, 2, 3, 1, 2, 3...
            claim.addMarker(IReinsuranceContractMarker, contract[(it-1) % 2])  // contract 1, 2, 1, 2, 1, 2...
            claim
        }
        [0, 1, 2, 4].each { claim[it].addMarker(ISegmentMarker, lob['A']) }
        claim[3].addMarker(ISegmentMarker, lob['B'])
        claim[6].addMarker(ISegmentMarker, lob['C'])
        
        // claim        1   2   3   4   5   6   7   8   9   10  11  12
        // peril        1   2   3   1   2   3   1   2   3   1   2   3
        // contract     1   2   1   2   1   2   1   2   1   2   1   2
        // lob          A   A   A   B   A   -   C   -   -   -   -   -
        // Note:
        // Claim X belongs to contract Y if X & Y are congruent mod 2: claims 1, 3, 5, 7... belong to contract 1, claims 2, 4, 6, 8... to contract 2.
        // Claim X belongs to peril Y if X & Y are congruent mod 3: claims 1, 4, 7... belong to peril 1, claims 2, 5, 8... to peril 2, claims 3, 6, 9.. to peril 3.
        // Each (peril, contract) pair will have two claims, one in the range 1-6 and the other in the range 7-12.
        // The (peril, lob) pairs 1A, 1B, 1C, 2A & 3A have claims 1, 4, 7, {2,5} & 3 respectively.
    }

    void testUsageFilterClaimsByPerilLobReserve() {

        // from setUp, this test case uses this information:
        // claim        1   2   3   4   5   6   7   8   9   10  11  12
        // peril        1   2   3   1   2   3   1   2   3   1   2   3
        // lob          A   A   A   B   A   -   C   -   -   -   -   -
        // in these sub-cases (for reserves filter tests, the peril markers are used):
        //              x           x           x           x           peril 1
        //              x   x   x       x                               lob A
        //              x                                               peril 1 & lob A
        //                  x           x                               peril 2 & lob A

        List<IPerilMarker> perils = [peril[0]]
        List<IPerilMarker> reserves = null
        List<ISegmentMarker> lobs = null
        List filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, null)
        assertEquals 'filtered claims, peril 1: claims 1,4,7',
                [0,3,6,9].join(", "),
                filteredClaims.collect {claim.indexOf(it)}.join(", ")
        assertEquals '#filtered claims, peril 1', 4, filteredClaims.size()
        assertEquals 'filtered claims, peril 1: claim 1', claim[0], filteredClaims[0]
        assertEquals 'filtered claims, peril 1: claim 4', claim[3], filteredClaims[1]
        assertEquals 'filtered claims, peril 1: claim 7', claim[6], filteredClaims[2]
        assertEquals 'filtered claims, peril 1: claim 10', claim[9], filteredClaims[3]

        perils = [peril[0]]
        lobs = [lob['A']]
        reserves = null
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, LogicArguments.AND)
        assertEquals '#filtered claims, peril 1 & lob A', 1, filteredClaims.size()
        assertEquals 'filtered claims, peril 1 & lob A: claim 1', claim[0], filteredClaims[0]

        perils = null
        lobs = [lob['A']]
        reserves = null
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, null)
        assertEquals '#filtered claims, lob A', 4, filteredClaims.size()
        assertEquals 'filtered claims, lob A: claim 1', claim[0], filteredClaims[0]
        assertEquals 'filtered claims, lob A: claim 2', claim[1], filteredClaims[1]
        assertEquals 'filtered claims, lob A: claim 3', claim[2], filteredClaims[2]
        assertEquals 'filtered claims, lob A: claim 5', claim[4], filteredClaims[3]

        perils = null
        lobs = null
        reserves = [peril[0]]
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, null)
        assertEquals '#filtered claims, reserve = peril 1', 4, filteredClaims.size()
        assertEquals 'filtered claims, reserve = peril 1: claim 1', claim[0], filteredClaims[0]
        assertEquals 'filtered claims, reserve = peril 1: claim 4', claim[3], filteredClaims[1]
        assertEquals 'filtered claims, reserve = peril 1: claim 7', claim[6], filteredClaims[2]
        assertEquals 'filtered claims, reserve = peril 1: claim 10', claim[9], filteredClaims[3]

        perils = null
        lobs = [lob['A']]
        reserves = [peril[1]]
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, LogicArguments.AND)
        assertEquals '#filtered claims, reserve = peril 1 & lob A', 2, filteredClaims.size()
        assertEquals 'filtered claims, reserve = peril 1 & lob A: claim 2', claim[1], filteredClaims[0]
        assertEquals 'filtered claims, reserve = peril 1 & lob A: claim 5', claim[4], filteredClaims[1]

        perils = [peril[1]]
        lobs = [lob['A']]
        reserves = [peril[0]]
        shouldFail IllegalArgumentException, { ClaimFilterUtilities.filterClaimsByPerilLobReserve(claim, perils, lobs, reserves, LogicArguments.AND) }
    }

    void testUsageFilterClaimsByPerilContract() {

        // from setUp, this test case uses this information:
        // claim        1   2   3   4   5   6   7   8   9   10  11  12
        // peril        1   2   3   1   2   3   1   2   3   1   2   3
        // contract     1   2   1   2   1   2   1   2   1   2   1   2

        // peril 1 => claim 1, 4, 7...; contract 1 => claim 1, 3, 5, 7..; together (ANDed), we expect claim 1 & 7
        List filteredClaims = ClaimFilterUtilities.filterClaimsByPerilContract(claim, [peril[0]], ["contract 1"], LogicArguments.AND)
        assertEquals '#filtered claims (peril 1, contract 1)', 2, filteredClaims.size()
        assertEquals 'filtered claim 1 (peril 1, contract 1) = claim 1', claim[0], filteredClaims[0]
        assertEquals 'filtered claim 2 (peril 1, contract 1) = claim 7', claim[6], filteredClaims[1]

        // peril 1 => claim 1, 4, 7...; contract 1 => claim 1, 3, 5, 7..; together (ORed), we expect claim 1, 3, 4, 5, 7, 9, 10, 11 (odd, or congruent to 4 mod 6)
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilContract(claim, [peril[0]], ["contract 1"], LogicArguments.OR)
        assertEquals '#filtered claims (peril 1 OR contract 1)', 8, filteredClaims.size()
        assertEquals 'filtered claims: all but 2, 6, 8 & 12',
                ([1,3,4,5,7,9,10,11].collect {"Claim ${it}"}).join(", "),
                (filteredClaims.collect {it.senderChannelName}).join(", ")

        // congruent to 1 or 2 mod 3 and to 1 mod 2 is equivalent to being congruent to 1 or 5 mod 6,
        // so here we want claims 1, 5, 7, 11, i.e. all those whose claim number (1..12) is relatively prime to 6.
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilContract(claim, [peril[0], peril[1]], ["contract 1"], LogicArguments.AND)
        assertEquals '#filtered claims (peril 1, contract 1)', 4, filteredClaims.size()
        assertEquals 'filtered claims: relatively prime to 6',
                ([1, 5, 7, 11].collect {"Claim ${it}"}).join(", "),
                (filteredClaims.collect {it.senderChannelName}).join(", ")
    }

    void testDegenerateUsageFilterClaimsByPerilContract() {

        // no criteria (no list of perils or contracts) are given to filter by
        shouldFail IllegalArgumentException, { ClaimFilterUtilities.filterClaimsByPerilContract(claim, null, null, null) }

        // no criteria (empty lists of perils and contracts) are given to filter by
        shouldFail IllegalArgumentException, { ClaimFilterUtilities.filterClaimsByPerilContract(claim, [], [], null) }

        // no logical join/connection type is specified when both lists, of perils and of contracts, are nonempty
        shouldFail IllegalArgumentException, { ClaimFilterUtilities.filterClaimsByPerilContract(claim, [peril[0]], ["contract 1"], null) }
    }
}

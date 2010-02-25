package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.domain.pc.reserves.cashflow.ClaimDevelopmentPacket
import org.pillarone.riskanalytics.domain.pc.generators.claims.PerilMarker
import org.pillarone.riskanalytics.domain.pc.lob.LobMarker
import org.pillarone.riskanalytics.domain.pc.constants.LogicArguments

/**
 * @author shartmann (at) munichre (dot) com
 */
class ClaimFilterUtilitiesTests extends GroovyTestCase {

    void testUsageFilterClaimsByPerilLobReserve() {
        TestPerilComponent perilA = new TestPerilComponent()
        TestPerilComponent perilB = new TestPerilComponent()
        TestPerilComponent perilC = new TestPerilComponent()
        TestLobComponent lobA = new TestLobComponent()
        TestLobComponent lobB = new TestLobComponent()
        TestLobComponent lobC = new TestLobComponent()
        List<ClaimDevelopmentPacket> claims = []
        ClaimDevelopmentPacket claims1000 = new ClaimDevelopmentPacket(incurred: 1000, paid: 800, peril: perilA, lineOfBusiness: lobA)
        ClaimDevelopmentPacket claims900 = new ClaimDevelopmentPacket(incurred: 900, paid: 800, peril: perilA, lineOfBusiness: lobB)
        ClaimDevelopmentPacket claims800 = new ClaimDevelopmentPacket(incurred: 800, paid: 400, peril: perilA, lineOfBusiness: lobC)
        ClaimDevelopmentPacket claims700 = new ClaimDevelopmentPacket(incurred: 700, paid: 500, peril: perilB, lineOfBusiness: lobA)
        ClaimDevelopmentPacket claims600 = new ClaimDevelopmentPacket(incurred: 600, paid: 400, peril: perilB, lineOfBusiness: lobA)
        ClaimDevelopmentPacket claims500 = new ClaimDevelopmentPacket(incurred: 500, paid: 400, peril: perilC, lineOfBusiness: lobA)
        claims << claims1000 << claims500 << claims600 << claims700 << claims800 << claims900

        List<PerilMarker> perils = [perilA]
        List<PerilMarker> reserves = []
        List<LobMarker> lobs = []
        List filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, null)
        assertEquals '#filtered claims peril A', 3, filteredClaims.size()
        assertEquals 'filtered claims peril A, claims1000', claims1000, filteredClaims[0]
        assertEquals 'filtered claims peril A, claims800', claims800, filteredClaims[1]
        assertEquals 'filtered claims peril A, claims900', claims900, filteredClaims[2]

        perils = [perilA]
        reserves = []
        lobs = [lobA]
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, LogicArguments.AND)
        assertEquals '#filtered claims peril A', 1, filteredClaims.size()
        assertEquals 'filtered claims peril A, claims1000', claims1000, filteredClaims[0]
        
        perils = []
        reserves = []
        lobs = [lobA]
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, null)
        assertEquals '#filtered claims peril A', 4, filteredClaims.size()
        assertEquals 'filtered claims peril A, claims1000', claims1000, filteredClaims[0]
        assertEquals 'filtered claims peril A, claims500', claims500, filteredClaims[1]
        assertEquals 'filtered claims peril A, claims600', claims600, filteredClaims[2]
        assertEquals 'filtered claims peril A, claims700', claims700, filteredClaims[3]

        perils = []
        reserves = [perilA]
        lobs = []
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, null)
        assertEquals '#filtered claims peril A', 3, filteredClaims.size()
        assertEquals 'filtered claims peril A, claims1000', claims1000, filteredClaims[0]
        assertEquals 'filtered claims peril A, claims800', claims800, filteredClaims[1]
        assertEquals 'filtered claims peril A, claims900', claims900, filteredClaims[2]

        perils = []
        reserves = [perilB]
        lobs = [lobA]
        filteredClaims = ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, LogicArguments.AND)
        assertEquals '#filtered claims peril A', 2, filteredClaims.size()
        assertEquals 'filtered claims peril A, claims600', claims600, filteredClaims[0]
        assertEquals 'filtered claims peril A, claims700', claims700, filteredClaims[1]

        perils = [perilB]
        reserves = [perilA]
        lobs = [lobA]
        shouldFail IllegalArgumentException, { ClaimFilterUtilities.filterClaimsByPerilLobReserve(claims, perils, lobs, reserves, LogicArguments.AND) }
    }
}





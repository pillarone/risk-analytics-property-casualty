package models.correlatedAttritionalClaims

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.domain.pc.generators.copulas.LobCopula
import org.pillarone.riskanalytics.domain.pc.severities.ProbabilityExtractor
import org.pillarone.riskanalytics.domain.pc.generators.claims.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.claims.allocation.RiskAllocator

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CorrelatedAttritionalClaimsModel extends StochasticModel {

    LobCopula copula
    ProbabilityExtractor extractorFire
    ProbabilityExtractor extractorHull
    //  ProbabilityExtractor extractorMTPL
    AttritionalClaimsGenerator fireClaims
    AttritionalClaimsGenerator hullClaims
    // AttritionalClaimsGenerator mtplClaims
    RiskAllocator claimsAggregator


    void initComponents() {
        copula = new LobCopula()
        extractorFire = new ProbabilityExtractor()
        extractorHull = new ProbabilityExtractor()
        //   extractorMTPL = new ProbabilityExtractor()
        fireClaims = new AttritionalClaimsGenerator()
        hullClaims = new AttritionalClaimsGenerator()
        //     mtplClaims = new AttritionalClaimsGenerator()
        claimsAggregator = new RiskAllocator()

        addStartComponent copula
    }

    void wireComponents() {
        extractorFire.inProbabilities = copula.outProbabilities
        extractorHull.inProbabilities = copula.outProbabilities
        // extractorMTPL.inProbabilities = copulas.outProbabilities
        fireClaims.inProbability = extractorFire.outProbabilities
        hullClaims.inProbability = extractorHull.outProbabilities
        //mtplClaims.inProbability = extractorMTPL.outProbabilities
        claimsAggregator.inClaims = fireClaims.outClaims
        claimsAggregator.inClaims = hullClaims.outClaims
        //claimsAggregator.inClaimsGross = mtplClaims.outClaims

    }
}
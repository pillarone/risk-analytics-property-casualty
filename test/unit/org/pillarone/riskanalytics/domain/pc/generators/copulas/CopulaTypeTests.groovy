package org.pillarone.riskanalytics.domain.pc.generators.copulas

import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CopulaTypeTests extends GroovyTestCase {

    static Copula getCopula() {
        return new LobCopula(
            parmCopulaStrategy: CopulaStrategyFactory.getCopulaStrategy(
                LobCopulaType.INDEPENDENT, ["targets": new SimpleMultiDimensionalParameter(["Fire", "Hull", "Legal"])]))
    }

    void testValueOf() {
        LobCopulaType copulaType = new LobCopulaType("foo", "FOO", ["paramKey": "paramValue"])
        assertEquals copulaType, copulaType.valueOf("foo")
    }

    // todo(sku): discuss with mhu if this test is sufficient
    void testConstructionString() {
        LobCopulaType copulaType = new LobCopulaType("foo", "FOO", null)
        String constructionString = copulaType.getConstructionString(["paramKey": "paramValue"])
        assertTrue constructionString.contains(copulaType.valueOf("foo").toString().toUpperCase())
        assertTrue constructionString.contains("paramKey")
        assertTrue constructionString.contains("paramValue")
    }
}
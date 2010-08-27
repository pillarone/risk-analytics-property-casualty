package org.pillarone.riskanalytics.domain.pc.reinsurance

import org.pillarone.riskanalytics.domain.pc.creditrisk.DefaultProbabilities
import org.pillarone.riskanalytics.domain.assets.constants.Rating
import org.pillarone.riskanalytics.domain.pc.creditrisk.ReinsurersDefault
import org.pillarone.riskanalytics.domain.pc.constants.Reinsurer

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class ReinsurersDefaultProbabilityTests extends GroovyTestCase {

    /*static Map defaults =  [Rating.AAA: 0.0001,
                 Rating.AA: 0.0002,
                 Rating.A: 0.0005]*/
    static DefaultProbabilities defaultProbabilities = new DefaultProbabilities(
        defaultProbability:
        [(Rating.AAA): 0d,
            (Rating.AA): 0.0005d,
            (Rating.A): 0.001d,
            (Rating.BBB): 0.005d,
            (Rating.BB): 0.05d,
            (Rating.B): 0.09d,
            (Rating.CCC): 0.15d,
            (Rating.CC): 0.25d,
            (Rating.C): 0.35d,
            (Rating.DEFAULT): 1d])

    void testUsage() {
        ReinsurersDefaultProbability reinsurersDefaultProbability = new ReinsurersDefaultProbability()
        reinsurersDefaultProbability.parmEarthRe = Rating.A
        reinsurersDefaultProbability.parmJupiterRe = Rating.BBB
        reinsurersDefaultProbability.parmMarsRe = Rating.CC
        reinsurersDefaultProbability.parmMercuryRe = Rating.DEFAULT
        reinsurersDefaultProbability.inDefaultProbability.add defaultProbabilities

        reinsurersDefaultProbability.doCalculation()
        assertEquals "one packet", 1, reinsurersDefaultProbability.outReinsurersDefault.size()
        ReinsurersDefault reinsurersDefault = (ReinsurersDefault) reinsurersDefaultProbability.outReinsurersDefault[0]
        assertEquals "default for every reinsurer available", Reinsurer.values().length, reinsurersDefault.defaultOccured.size()
    }

    void testAllDefault() {
        ReinsurersDefaultProbability reinsurersDefaultProbability = new ReinsurersDefaultProbability()
        reinsurersDefaultProbability.parmEarthRe = Rating.DEFAULT
        reinsurersDefaultProbability.parmJupiterRe = Rating.DEFAULT
        reinsurersDefaultProbability.parmMarsRe = Rating.DEFAULT
        reinsurersDefaultProbability.parmMercuryRe = Rating.DEFAULT
        reinsurersDefaultProbability.parmMercuryRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmVenusRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmEarthRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmMarsRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmJupiterRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmSaturnRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmUranusRe = Rating.DEFAULT;
        reinsurersDefaultProbability.parmNeptuneRe = Rating.DEFAULT;
        reinsurersDefaultProbability.inDefaultProbability.add defaultProbabilities

        reinsurersDefaultProbability.doCalculation()
        assertEquals "one packet", 1, reinsurersDefaultProbability.outReinsurersDefault.size()
        ReinsurersDefault reinsurersDefault = (ReinsurersDefault) reinsurersDefaultProbability.outReinsurersDefault[0]
        assertEquals "default for every reinsurer available", Reinsurer.values().length, reinsurersDefault.defaultOccured.size()
        reinsurersDefault.defaultOccured.values().each { assertEquals 'all reinsurers get default', 1, it }
    }

    void testNoDefault() {
        ReinsurersDefaultProbability reinsurersDefaultProbability = new ReinsurersDefaultProbability()
        reinsurersDefaultProbability.inDefaultProbability.add defaultProbabilities

        reinsurersDefaultProbability.doCalculation()
        assertEquals "one packet", 1, reinsurersDefaultProbability.outReinsurersDefault.size()
        ReinsurersDefault reinsurersDefault = (ReinsurersDefault) reinsurersDefaultProbability.outReinsurersDefault[0]
        assertEquals "default for every reinsurer available", Reinsurer.values().length, reinsurersDefault.defaultOccured.size()
        reinsurersDefault.defaultOccured.values().each { assertEquals 'no reinsurer gets default', 0, it }
    }
}
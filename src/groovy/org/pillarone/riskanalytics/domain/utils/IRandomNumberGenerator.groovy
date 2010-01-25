package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution

interface IRandomNumberGenerator {

    Number nextValue()
    Distribution getDistribution()
}
package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.probdist.Distribution

@Deprecated
interface IRandomNumberGenerator {

    Number nextValue()
    Distribution getDistribution()
}
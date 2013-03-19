package org.pillarone.riskanalytics.domain.utils

import umontreal.iro.lecuyer.randvar.RandomVariateGen

/**
 * @author: stefan.kunz (at) intuitive-collaboration (dot) com
 */
@Deprecated
class ConstantVariateGenerator extends RandomVariateGen {
    double constant

    double nextDouble() {
        constant
    }
}
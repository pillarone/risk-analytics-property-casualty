package org.pillarone.riskanalytics.domain.pc.underwriting;

import org.pillarone.riskanalytics.domain.utils.PacketUtilities;


/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureInfoUtilities {
    // todo(sku): use dko if there is a more general solution possible
    public static boolean sameContent(ExposureInfo expInfo1, ExposureInfo expInfo2) {
        return (PacketUtilities.sameContent(expInfo1, expInfo2)
                && expInfo1.premiumWrittenAsIf == expInfo2.premiumWrittenAsIf
                && expInfo1.numberOfPolicies == expInfo2.numberOfPolicies
                && expInfo1.sumInsured == expInfo2.sumInsured
                && expInfo1.maxSumInsured == expInfo2.maxSumInsured
                && PacketUtilities.equals(expInfo1.exposureDefinition, expInfo2.exposureDefinition));
    }
}

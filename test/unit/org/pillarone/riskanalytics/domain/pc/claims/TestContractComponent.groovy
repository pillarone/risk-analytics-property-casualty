package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author ben.ginsberg (at) intuitive-collaboration.com
 */
class TestContractComponent extends Component implements IReinsuranceContractMarker {

    protected void doCalculation() {
    }

    boolean adjustExposureInfo() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }

    boolean isProportionalContract() {
        return false  //To change body of implemented methods use File | Settings | File Templates.
    }
}
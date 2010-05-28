package org.pillarone.riskanalytics.domain.pc.claims

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.reinsurance.contracts.IReinsuranceContractMarker

/**
 * @author ben.ginsberg (at) intuitive-collaboration.com
 */
class TestContractComponent extends Component implements IReinsuranceContractMarker {

    protected void doCalculation() {
    }
}
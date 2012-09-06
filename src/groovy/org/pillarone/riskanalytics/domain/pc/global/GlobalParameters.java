package org.pillarone.riskanalytics.domain.pc.global;

import org.pillarone.riskanalytics.core.components.GlobalParameterComponent;
import org.pillarone.riskanalytics.core.parameterization.global.Global;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GlobalParameters extends GlobalParameterComponent {

    private boolean runtimeSanityChecks = true;

    @Global(identifier = "sanityChecks")
    public boolean isRuntimeSanityChecks() {
        return runtimeSanityChecks;
    }

    public void setRuntimeSanityChecks(boolean runtimeSanityChecks) {
        this.runtimeSanityChecks = runtimeSanityChecks;
    }
}

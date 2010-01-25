package org.pillarone.riskanalytics.domain.pc.claims;

import org.pillarone.riskanalytics.domain.pc.underwriting.ExposureInfo;

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */
public class ClaimWithExposure extends Claim {

    public ExposureInfo exposure;

    public ClaimWithExposure() {
        super();
    }

    @Override
    public Claim copy() {
        ClaimWithExposure copy = new ClaimWithExposure();
        copy.set(this);
        copy.setExposure(exposure);
        return copy;
    }

    @Override
    public ClaimWithExposure clone() {
        ClaimWithExposure clonedClaim = (ClaimWithExposure) super.clone();
        clonedClaim.setExposure(this.exposure);
        return clonedClaim;
    }

    public ExposureInfo getExposure() {
        return exposure;
    }

    public void setExposure(ExposureInfo exposure) {
        this.exposure = exposure;
    }
}
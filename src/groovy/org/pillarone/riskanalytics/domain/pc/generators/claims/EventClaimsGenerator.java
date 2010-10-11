package org.pillarone.riskanalytics.domain.pc.generators.claims;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;
import org.pillarone.riskanalytics.domain.pc.claims.ClaimPacketFactory;
import org.pillarone.riskanalytics.domain.pc.constants.ClaimType;
import org.pillarone.riskanalytics.domain.pc.generators.severities.Event;
import org.pillarone.riskanalytics.domain.pc.generators.severities.EventSeverity;
import org.pillarone.riskanalytics.domain.utils.DistributionType;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;

/**
 * The event claims generator sends claims object to components
 * attached to the <tt>outClaims</tt> according the number received
 * over the input channel <tt>inSeverities</tt>.<br/>
 * Claims are generated according to the parameterization of <tt>parmGenerator</tt>.
 * Among the possible distributions are normal, lognormal, pareto, uniform,
 * poisson and negative binomial.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventClaimsGenerator extends ClaimsGenerator {
    private RandomDistribution parmDistribution = DistributionType.getUniformDistribution();

    /**
     * Input channel for claims severities to be generated
     */
    private PacketList<EventSeverity> inSeverities = new PacketList<EventSeverity>(EventSeverity.class);


    public void doCalculation() {
        if (parmDistribution == null) {
            throw new IllegalStateException("EventClaimsGenerator.missingRandomVariateDistribution");
        }

        for (EventSeverity severity : inSeverities) {
            double claimSize = parmDistribution.getDistribution().inverseF(severity.value);

            Claim claim = ClaimPacketFactory.createPacket();
            claim.origin = this;
            claim.setEvent(severity.event);
            claim.setClaimType(ClaimType.EVENT);
            claim.setUltimate(claimSize);
            claim.setFractionOfPeriod(severity.getEvent().getDate());

            getOutClaims().add(claim);
        }
        if (getOutClaims().isEmpty()) {
            Claim claim = ClaimPacketFactory.createPacket();
            claim.origin = this;
            claim.setEvent(new Event());
            claim.getEvent().setDate(0d);
            claim.setClaimType(ClaimType.EVENT);
            claim.setUltimate(0d);
            claim.setFractionOfPeriod(claim.getEvent().getDate());
            getOutClaims().add(claim);
        }
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public PacketList<EventSeverity> getInSeverities() {
        return inSeverities;
    }

    public void setInSeverities(PacketList<EventSeverity> inSeverities) {
        this.inSeverities = inSeverities;
    }
}
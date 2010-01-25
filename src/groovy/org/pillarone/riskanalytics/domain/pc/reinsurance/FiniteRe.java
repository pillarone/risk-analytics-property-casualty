package org.pillarone.riskanalytics.domain.pc.reinsurance;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.pc.claims.Claim;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FiniteRe extends Component {

    private PacketList<Claim> inClaims = new PacketList<Claim>(Claim.class);
    private PacketList<ExperienceAccountPacket> outExperienceAccount = new PacketList<ExperienceAccountPacket>(ExperienceAccountPacket.class);
    private PacketList<ReinsuranceResultPacket> outReinsurance = new PacketList<ReinsuranceResultPacket>(ReinsuranceResultPacket.class);

    private double parmPremium = 0d;
    private double parmFractionExperienceAccount = 0d;

    private PeriodStore periodStore;

    private static final String EAB = "EAB";
    private static final String RESULT = "RESULT";

    protected void doCalculation() {
        double totalIncomingClaims = 0;
        for (Claim claim : inClaims) {
            totalIncomingClaims += claim.getUltimate();
        }

        double priorPeriodBalance = periodStore.exists(EAB) ? ((ExperienceAccountPacket) periodStore.get(EAB, -1)).getBalance() : 0;
        ExperienceAccountPacket experienceAccount = new ExperienceAccountPacket();
        experienceAccount.setPremium(parmPremium * parmFractionExperienceAccount);
        experienceAccount.setClaim(Math.min(priorPeriodBalance + experienceAccount.getPremium(), totalIncomingClaims));
        experienceAccount.setNetCashFlow(experienceAccount.getPremium() - experienceAccount.getClaim());
        experienceAccount.setBalance(priorPeriodBalance + experienceAccount.getPremium() - experienceAccount.getClaim());
        periodStore.put(EAB, experienceAccount);
        outExperienceAccount.add(experienceAccount);

        double priorPeriodResult = periodStore.exists(RESULT) ? ((ReinsuranceResultPacket) periodStore.get(RESULT, -1)).getResult() : 0;
        ReinsuranceResultPacket reinsurance = new ReinsuranceResultPacket();
        reinsurance.setPremium(parmPremium * (1 - parmFractionExperienceAccount));
        reinsurance.setClaim(totalIncomingClaims - experienceAccount.getClaim());
        reinsurance.setNetCashFlow(reinsurance.getPremium() - reinsurance.getClaim());
        reinsurance.setResult(priorPeriodResult + reinsurance.getPremium() - reinsurance.getClaim());
        periodStore.put(RESULT, reinsurance);
        outReinsurance.add(reinsurance);
    }

    public PacketList<Claim> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<Claim> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<ExperienceAccountPacket> getOutExperienceAccount() {
        return outExperienceAccount;
    }

    public void setOutExperienceAccount(PacketList<ExperienceAccountPacket> outExperienceAccount) {
        this.outExperienceAccount = outExperienceAccount;
    }

    public PacketList<ReinsuranceResultPacket> getOutReinsurance() {
        return outReinsurance;
    }

    public void setOutReinsurance(PacketList<ReinsuranceResultPacket> outReinsurance) {
        this.outReinsurance = outReinsurance;
    }

    public double getParmPremium() {
        return parmPremium;
    }

    public void setParmPremium(double parmPremium) {
        this.parmPremium = parmPremium;
    }

    public double getParmFractionExperienceAccount() {
        return parmFractionExperienceAccount;
    }

    public void setParmFractionExperienceAccount(double parmFractionExperienceAccount) {
        this.parmFractionExperienceAccount = parmFractionExperienceAccount;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}

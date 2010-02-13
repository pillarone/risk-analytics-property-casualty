package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumCalculation extends Component {

  double parmNumberOfPolicy
  double parmPricePerPolicy

  PacketList<PremiumPacket> outPremium = new PacketList<PremiumPacket>(PremiumPacket)

  void doCalculation() {
      double premium = parmNumberOfPolicy * parmPricePerPolicy
      outPremium << new PremiumPacket(value: premium)
    }
}
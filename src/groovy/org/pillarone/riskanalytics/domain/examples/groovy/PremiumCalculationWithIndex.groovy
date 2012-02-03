package org.pillarone.riskanalytics.domain.examples.groovy

import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PremiumCalculationWithIndex extends PremiumCalculation{

  void doCalculation() {
      double level = 1.0
      for (IndexPacket idx in inIndex) {
          level *= idx.value
      }
      double pricePerPolicy = parmPricePerPolicy * level
      double premium = parmNumberOfPolicy * pricePerPolicy
      outPremium << new PremiumPacket(value: premium)
    }
}
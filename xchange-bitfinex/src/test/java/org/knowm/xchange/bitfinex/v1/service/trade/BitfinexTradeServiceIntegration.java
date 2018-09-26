package org.knowm.xchange.bitfinex.v1.service.trade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitfinex.v1.service.BitfinexProperties;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

public class BitfinexTradeServiceIntegration {

  private BitfinexProperties properties = new BitfinexProperties();

  public BitfinexTradeServiceIntegration() throws IOException {}

  @Before
  public void setup() throws IOException {
    properties = new BitfinexProperties();
    Assume.assumeTrue("Ignore tests because credentials are missing", properties.isValid());
  }

  @Test
  public void placeLimitOrderAndModifyItTest() throws Exception {

    Exchange exchange =
        ExchangeFactory.INSTANCE.createExchange(
            BitfinexExchange.class.getName(), properties.getApiKey(), properties.getSecretKey());

    LimitOrder limitOrder1 =
        new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD)
            .originalAmount(new BigDecimal(0.001))
            .limitPrice(new BigDecimal(20000))
            .build();

    String orderId1 = exchange.getTradeService().placeLimitOrder(limitOrder1);
    assertThat(orderId1).isNotBlank();

    LimitOrder limitOrder2 =
        new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD)
            .originalAmount(new BigDecimal(0.001))
            .limitPrice(new BigDecimal(30000))
            .id(orderId1)
            .build();

    String orderId2 = exchange.getTradeService().modifyOrder(limitOrder2);
    assertThat(orderId2).isNotBlank();
  }
}

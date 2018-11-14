package org.knowm.xchange.cexio.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.cexio.CexIOExchange;
import org.knowm.xchange.cexio.CexioProperties;
import org.knowm.xchange.cexio.dto.trade.CexIOOrderWithTransactions;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class TradeServiceIntegration {
  private CexioProperties properties;
  private Exchange exchange;
  private CexIOTradeService tradeService;
  private LimitOrder order;

  @Before
  public void setup() throws IOException {
    properties = new CexioProperties();
    exchange = ExchangeFactory.INSTANCE.createExchange(CexIOExchange.class.getName());

    ExchangeSpecification specification = exchange.getDefaultExchangeSpecification();
    specification.setApiKey(properties.getApiKey());
    specification.setSecretKey(properties.getSecretKey());
    specification.setUserName(properties.getUserName());

    exchange.applySpecification(specification);

    tradeService = (CexIOTradeService) exchange.getTradeService();

    order =
        buildOrder(
            Order.OrderType.BID,
            CurrencyPair.BCH_USD,
            BigDecimal.valueOf(300),
            BigDecimal.valueOf(0.02));
  }

  @Test
  public void getOrderTransactionsTest() throws IOException, InterruptedException {

    String orderId = tradeService.placeLimitOrder(order);

    tradeService.cancelOrder(orderId);

    Thread.sleep(2000);

    CexIOOrderWithTransactions orderWithTransactions = tradeService.getOrderTransactions(orderId);

    Assert.assertEquals(
        "Order id from transaction, must equals requested order id",
        orderId,
        orderWithTransactions.getId());

    Assert.assertTrue(
        "Order amount from transaction, must equal sent order amount",
        order.getOriginalAmount().compareTo(orderWithTransactions.getAmount()) == 0);

    Assert.assertTrue(
        "Transaction list must not be empty", orderWithTransactions.getVtx().size() > 0);
  }

  @Test
  public void orderPlaceGetCancelTest() throws IOException, InterruptedException {
    String orderId = tradeService.placeLimitOrder(order);

    tradeService.cancelOrder(orderId);

    List<Order> orders = (List<Order>) tradeService.getOrder(orderId);

    Assert.assertTrue("Order response must contain 1 order", orders.size() == 1);

    Assert.assertTrue(
        "Returned order id must be the same as placed", orderId.equals(orders.get(0).getId()));

    Assert.assertTrue(
        "Returned order must be canceled", orders.get(0).getStatus() == Order.OrderStatus.CANCELED);
  }

  public LimitOrder buildOrder(
      Order.OrderType orderType, CurrencyPair pair, BigDecimal price, BigDecimal amount) {
    return new LimitOrder.Builder(orderType, pair).limitPrice(price).originalAmount(amount).build();
  }
}

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
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;

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

  @Test
  public void CancelOrderByCurrencyPair() throws IOException, InterruptedException {
    String orderId = tradeService.placeLimitOrder(order);
    String orderId2 = tradeService.placeLimitOrder(order);

    tradeService.cancelOrder((CancelOrderByCurrencyPair) () -> new CurrencyPair("BCH/USD"));

    List<Order> orders = (List<Order>) tradeService.getOrder(orderId, orderId2);

    Assert.assertTrue("Order response must contain 2 orders", orders.size() == 2);
    Assert.assertTrue(
            "Returned order 1 id must be the same as placed", orderId.equals(orders.get(0).getId()));
    Assert.assertTrue(
            "Returned order 2 id must be the same as placed", orderId2.equals(orders.get(1).getId()));
    Assert.assertTrue(
            "Order 1 must be canceled", orders.get(0).getStatus() == Order.OrderStatus.CANCELED);
    Assert.assertTrue(
            "Order 2 must be canceled", orders.get(1).getStatus() == Order.OrderStatus.CANCELED);
  }

  @Test
  public void modifyOrder() throws IOException, InterruptedException {
    BigDecimal modifyPrice = new BigDecimal(302);
    BigDecimal endPrice = new BigDecimal(304);

    String orderId = tradeService.placeLimitOrder(order);

    LimitOrder order2 = new LimitOrder(order.getType(), order.getOriginalAmount(), order.getCurrencyPair(),
            orderId, order.getTimestamp(), modifyPrice);
    String orderId2 = tradeService.modifyOrder(order2);

    LimitOrder order3 = new LimitOrder(order.getType(), order.getOriginalAmount(), order.getCurrencyPair(),
            orderId2, order.getTimestamp(), endPrice);
    String orderId3 = tradeService.modifyOrder(order3);

    List<Order> orders = (List<Order>) tradeService.getOrder(orderId, orderId2, orderId3);

    Assert.assertTrue("Order response must contain 1 order", orders.size() == 3);
    Assert.assertTrue(
            "Order 1 must be canceled", orders.get(0).getStatus() == Order.OrderStatus.CANCELED);
    Assert.assertTrue(
            "Order 2 must be canceled", orders.get(1).getStatus() == Order.OrderStatus.CANCELED);
    Assert.assertTrue(
            "Order 3 must be placed", orders.get(2).getStatus() == Order.OrderStatus.PENDING_NEW);
    Assert.assertTrue("Order 3 must have `endPrice` price",
            ((LimitOrder) orders.get(2)).getLimitPrice().compareTo(endPrice) == 0);

    tradeService.cancelOrder((CancelOrderByCurrencyPair) () -> new CurrencyPair("BCH/USD"));
  }

  public LimitOrder buildOrder(
      Order.OrderType orderType, CurrencyPair pair, BigDecimal price, BigDecimal amount) {
    return new LimitOrder.Builder(orderType, pair).limitPrice(price).originalAmount(amount).build();
  }
}

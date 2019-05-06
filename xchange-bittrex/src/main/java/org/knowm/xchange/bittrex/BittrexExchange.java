package org.knowm.xchange.bittrex;

import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bittrex.dto.BittrexException;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexCurrency;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexSymbol;
import org.knowm.xchange.bittrex.service.BittrexAccountService;
import org.knowm.xchange.bittrex.service.BittrexMarketDataService;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.knowm.xchange.bittrex.service.BittrexTradeService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.utils.nonce.AtomicLongIncrementalTime2013NonceFactory;
import si.mazi.rescu.SynchronizedValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BittrexExchange extends BaseExchange implements Exchange {

  private SynchronizedValueFactory<Long> nonceFactory =
      new AtomicLongIncrementalTime2013NonceFactory();

  @Override
  protected void initServices() {

    this.marketDataService = new BittrexMarketDataService(this);
    this.accountService = new BittrexAccountService(this);
    this.tradeService = new BittrexTradeService(this);
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {

    ExchangeSpecification exchangeSpecification =
        new ExchangeSpecification(this.getClass().getCanonicalName());
    exchangeSpecification.setSslUri("https://bittrex.com/api/");
    exchangeSpecification.setHost("bittrex.com");
    exchangeSpecification.setPort(80);
    exchangeSpecification.setExchangeName("Bittrex");
    exchangeSpecification.setExchangeDescription("Bittrex is a bitcoin and altcoin exchange.");

    return exchangeSpecification;
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {

    return nonceFactory;
  }

  @Override
  public void remoteInit() throws IOException, ExchangeException {
    try {
      BittrexMarketDataServiceRaw dataService =
          (BittrexMarketDataServiceRaw) this.marketDataService;

      List<BittrexSymbol> bittrexSymbols = dataService.getBittrexSymbols();
      List<BittrexCurrency> bittrexCurrencies = dataService.getBittrexCurrencies();

      Map<CurrencyPair, CurrencyPairMetaData> currencyPairMetaData =
          BittrexAdapters.adaptCurrencyPairMetaData(bittrexSymbols);

      Map<Currency, CurrencyMetaData> currencyMetaData =
          BittrexAdapters.adaptCurrencyMetaData(bittrexCurrencies);

      exchangeMetaData =
          new ExchangeMetaData(
              currencyPairMetaData,
              currencyMetaData,
              exchangeMetaData.getPublicRateLimits(),
              exchangeMetaData.getPrivateRateLimits(),
              exchangeMetaData.isShareRateLimits());
    } catch (BittrexException e) {
      throw BittrexErrorAdapter.adapt(e);
    }
  }
}

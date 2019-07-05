package org.knowm.xchange.bittrex.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BittrexSymbol {

  private final String baseCurrency;
  private final String baseCurrencyLong;
  private final String created;
  private final boolean isActive;
  private final boolean isRestricted;
  private final String marketCurrency;
  private final String marketCurrencyLong;
  private final String marketName;
  private final Number minTradeSize;

  public BittrexSymbol(
      @JsonProperty("BaseCurrency") String baseCurrency,
      @JsonProperty("BaseCurrencyLong") String baseCurrencyLong,
      @JsonProperty("Created") String created,
      @JsonProperty("IsActive") boolean isActive,
      @JsonProperty("IsRestricted") boolean isRestricted,
      @JsonProperty("MarketCurrency") String marketCurrency,
      @JsonProperty("MarketCurrencyLong") String marketCurrencyLong,
      @JsonProperty("MarketName") String marketName,
      @JsonProperty("MinTradeSize") Number minTradeSize) {

    this.baseCurrency = baseCurrency;
    this.baseCurrencyLong = baseCurrencyLong;
    this.created = created;
    this.isActive = isActive;
    this.isRestricted = isRestricted;
    this.marketCurrency = marketCurrency;
    this.marketCurrencyLong = marketCurrencyLong;
    this.marketName = marketName;
    this.minTradeSize = minTradeSize;
  }

  public String getBaseCurrency() {

    return this.baseCurrency;
  }

  public String getBaseCurrencyLong() {

    return this.baseCurrencyLong;
  }

  public String getCreated() {

    return this.created;
  }

  public boolean getIsActive() {

    return this.isActive;
  }

  public String getMarketCurrency() {

    return this.marketCurrency;
  }

  public String getMarketCurrencyLong() {

    return this.marketCurrencyLong;
  }

  public String getMarketName() {

    return this.marketName;
  }

  public Number getMinTradeSize() {

    return this.minTradeSize;
  }

  public boolean isRestricted() {

    return isRestricted;
  }

  @Override
  public String toString() {

    return "BittrexSymbol [baseCurrency="
        + baseCurrency
        + ", baseCurrencyLong="
        + baseCurrencyLong
        + ", created="
        + created
        + ", isActive="
        + isActive
        + ", isRestricted="
        + isRestricted
        + ", marketCurrency="
        + marketCurrency
        + ", marketCurrencyLong="
        + marketCurrencyLong
        + ", marketName="
        + marketName
        + ", minTradeSize="
        + minTradeSize
        + "]";
  }
}

package com.activequant.domainmodel;

import com.activequant.utils.annotations.Property;

public class TradeableInstrument extends PersistentEntity {

    private String instrumentId, tradingProvider, providerSpecificId;

    public TradeableInstrument(String clazz){
    	super(clazz);
    }
    
    public TradeableInstrument() {
        super(TradeableInstrument.class.getCanonicalName());
    }

    public TradeableInstrument(String tradProvider, String specId){
    	this.tradingProvider = tradProvider; 
    	this.providerSpecificId = specId;
    }
    
    
    public String getId() {
        return "TRADEABLE."+ tradingProvider +"." + providerSpecificId;
    }

    @Property
    public String getInstrumentId() {
        return instrumentId;
    }

    @Property
    public String getProviderSpecificId() {
        return providerSpecificId;
    }

    @Property
    public String getTradingProvider() {
        return tradingProvider;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public void setProviderSpecificId(String providerSpecificId) {
        this.providerSpecificId = providerSpecificId;
    }

    public void setTradingProvider(String tradingProvider) {
        this.tradingProvider = tradingProvider;
    }

}

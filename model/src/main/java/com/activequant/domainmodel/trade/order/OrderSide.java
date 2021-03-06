package com.activequant.domainmodel.trade.order;

/**
 * Enumeration to describe order side to take. 
 * 
 * @author GhostRider
 *
 */
public enum OrderSide {
    BUY(1), SELL(-1);
    private int side;

    private OrderSide(int side) {
        this.side = side;
    }

    /**
     * integer that can be used to multiply with a quantity 
     * @return 1 for buy, -1 for sell ...
     */
    public int getSide() {
        return this.side;
    }
    
    /**
     * 
     * @param side
     * @return
     */
    public static OrderSide valueFor(int side){
    	if(side==1)
    		return OrderSide.BUY; 
    	return OrderSide.SELL; 
    }
    
    
    /**
     * @return BUY for the buy, SELL for the sell value of this enum. 
     */
    public String toString() {
        if (side == 1)
            return "BUY";
        else
            return "SELL";
    }
}

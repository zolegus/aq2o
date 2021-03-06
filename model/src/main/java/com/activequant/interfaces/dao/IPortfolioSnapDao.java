package com.activequant.interfaces.dao;

import com.activequant.domainmodel.TimeStamp;
import com.activequant.domainmodel.backoffice.PortfolioSnap;

public interface IPortfolioSnapDao extends IEntityDao<PortfolioSnap>{
	 
    public PortfolioSnap loadSnapshot(String nonUniqueID, TimeStamp when);
        
	public PortfolioSnap[] loadSnapshots(String nonUniqueID, TimeStamp start, TimeStamp end);
}
package com.activequant.trading;

import com.activequant.interfaces.archive.IArchiveFactory;
import com.activequant.interfaces.dao.IDaoFactory;
import com.activequant.interfaces.trading.IExchange;
import com.activequant.interfaces.trading.ITradingSystemEnvironment;
import com.activequant.interfaces.transport.ITransportFactory;

/**
 * contains relevant pointers to key services. 
 * 
 * @author GhostRider
 *
 */
public class TradingSystemEnvironment implements ITradingSystemEnvironment {

	private IExchange exchange;
	private ITransportFactory transportFactory;
	private IArchiveFactory archiveFactory;
	private IDaoFactory daoFactory;
	private String workingDirectory = "./";
	

	public String getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public IExchange getExchange() {
		return exchange;
	}

	public void setExchange(IExchange exchange) {
		this.exchange = exchange;
	}

	public ITransportFactory getTransportFactory() {
		return transportFactory;
	}

	public void setTransportFactory(ITransportFactory transportFactory) {
		this.transportFactory = transportFactory;
	}

	public IArchiveFactory getArchiveFactory() {
		return archiveFactory;
	}

	public void setArchiveFactory(IArchiveFactory archiveFactory) {
		this.archiveFactory = archiveFactory;
	}

	public IDaoFactory getDaoFactory() {
		return daoFactory;
	}

	public void setDaoFactory(IDaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}

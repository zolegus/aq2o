package com.activequant.utils.recorder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.archive.IArchiveFactory;
import com.activequant.archive.IArchiveWriter;
import com.activequant.dao.IDaoFactory;
import com.activequant.domainmodel.PersistentEntity;
import com.activequant.domainmodel.TimeFrame;
import com.activequant.exceptions.TransportException;
import com.activequant.tools.streaming.MarketDataSnapshot;
import com.activequant.transport.ETransportType;
import com.activequant.transport.ITransportFactory;
import com.activequant.utils.events.IEventListener;
import com.activequant.utils.snmp.SNMPReporter;
import com.activequant.utils.snmp.SNMPReporter.ValueMode;

/**
 * A market data snapshot recorder. 
 * 
 * @author GhostRider
 *
 */
public class MDSRecorder {
	
	private IArchiveFactory archiveFactory;
	private ITransportFactory  transFac;
	private Logger log = Logger.getLogger(MDSRecorder.class);
	final Timer t = new Timer(true);
	final long collectionPhase = 5000l;
	private final ConcurrentLinkedQueue<MarketDataSnapshot> collectionList = new ConcurrentLinkedQueue<MarketDataSnapshot>();
	private SNMPReporter snmpReporter;
	
	private final IArchiveWriter rawWriter; 
	
	
	class InternalTimerTask extends TimerTask{
		int counter;
		@Override
		public void run() {
			
			Object o = collectionList.poll();
			counter = 0; 
			while(o!=null){
 
				o = collectionList.poll();
				store((MarketDataSnapshot)o);
			}
			log.info("Collected " + counter + " events. " );
			if(counter>0){
				try {
					rawWriter.commit();
					log.info("Committed.");
					snmpReporter.addValue("MDSEVENTS", counter);
				} catch (Exception e) {
					e.printStackTrace();
					log.warn("Error while committing. ", e);
				}
			}
			t.schedule(new InternalTimerTask() , (collectionPhase - System.currentTimeMillis()%collectionPhase));				
		}
		
		public void store(MarketDataSnapshot mds){
			if(mds==null)return;
			counter ++;
			String seriesId =mds.getMdiId();
			if(mds.getBidSizes()!=null && mds.getBidSizes().length>0){
				double bestBidPx = mds.getBidPrices()[0];
				double bestBidQ = mds.getBidSizes()[0];
				rawWriter.write(seriesId, mds.getTimeStamp(), "BID", bestBidPx);
				rawWriter.write(seriesId, mds.getTimeStamp(), "BIDQUANTITY", bestBidQ);
				if(log.isDebugEnabled())
					log.debug("Wrote " + seriesId + ", " + mds.getTimeStamp() + ", " + bestBidPx);
			}
			if(mds.getAskSizes()!=null && mds.getAskSizes().length>0){
				double bestAskPx = mds.getAskPrices()[0];
				double bestAskQ = mds.getAskSizes()[0];
				rawWriter.write(seriesId, mds.getTimeStamp(), "ASK", bestAskPx);
				rawWriter.write(seriesId, mds.getTimeStamp(), "ASKQUANTITY", bestAskQ);
			}
		}
		
	}
	
	public MDSRecorder(String springFile, String mdiFile) throws IOException, TransportException{
		
		ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[]{springFile});

		log.info("Starting up and fetching idf");
		IDaoFactory idf = (IDaoFactory) appContext.getBean("ibatisDao");
		archiveFactory = (IArchiveFactory) appContext.getBean("archiveFactory");
		log.info("Archive fetched.");
		rawWriter = archiveFactory.getWriter(TimeFrame.RAW);
		transFac = appContext.getBean("jmsTransport", ITransportFactory.class);
		log.info("Transport initialized.");
		subscribe(mdiFile);
		t.schedule(new InternalTimerTask(), (collectionPhase - System.currentTimeMillis()%collectionPhase));
//		t.schedule(new InternalTimerTask(), (collectionPhase - System.currentTimeMillis()%collectionPhase));
//		t.schedule(new InternalTimerTask(), (collectionPhase - System.currentTimeMillis()%collectionPhase));

        snmpReporter = new SNMPReporter(InetAddress.getLocalHost().getHostAddress(), 65001);
        snmpReporter.registerOID("MDSEVENTS", "1.3.6.1.1.0", ValueMode.VALUE);
		
	}
	
	
	private void subscribe(String mdiFile) throws IOException, TransportException{
		
		List<String> instruments = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mdiFile)));
		String l = br.readLine();
		while (l != null) {
			if (!l.startsWith("#") && !l.isEmpty()) {
				String symbol = l;
				int depth = 1; 
				if(l.indexOf(";")!=-1){
					String[] s = l.split(";");
					symbol = s[0];
					depth = Integer.parseInt(s[1]);
				}
				instruments.add(symbol);
			}
			l = br.readLine();
		}		
		for(String s : instruments){
			log.info("Subscribing to "  + s);
			transFac.getReceiver(ETransportType.MARKET_DATA, s).getMsgRecEvent().addEventListener(new IEventListener<PersistentEntity>() {
				@Override
				public void eventFired(PersistentEntity event) {
					System.out.print(".");
					if(event instanceof MarketDataSnapshot){
						collectionList.add((MarketDataSnapshot)event);
					}
				}
			});
		}		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws TransportException 
	 */
	public static void main(String[] args) throws IOException, TransportException {
		new MDSRecorder(args[0], args[1]);
	}

}

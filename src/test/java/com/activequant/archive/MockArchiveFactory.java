package com.activequant.archive;

import java.io.IOException;

import com.activequant.archive.IArchiveFactory;
import com.activequant.archive.IArchiveReader;
import com.activequant.archive.IArchiveWriter;
import com.activequant.domainmodel.TimeFrame;
import com.activequant.domainmodel.TimeStamp;
import com.activequant.domainmodel.Tuple;

public class MockArchiveFactory implements IArchiveFactory {

    @Override
    public IArchiveReader getReader(TimeFrame tf) {
        // TODO Auto-generated method stub
        return new IArchiveReader() {
            
            @Override
            public TimeSeriesIterator getTimeSeriesStream(String streamId, String key, TimeStamp startTimeStamp, TimeStamp stopTimeStamp) throws Exception {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public TSContainer getTimeSeries(String streamId, String key, TimeStamp startTimeStamp, TimeStamp stopTimeStamp) throws Exception {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public TSContainer getTimeSeries(String streamId, String key, TimeStamp startTimeStamp) throws Exception {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    public IArchiveWriter getWriter(TimeFrame tf) {
        // TODO Auto-generated method stub
        return new IArchiveWriter() {
            
            @Override
            public void write(String instrumentId, TimeStamp timeStamp, String key, Double value) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void write(String instrumentId, TimeStamp timeStamp, String[] keys, Double[] values) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void write(String instrumentId, TimeStamp timeStamp, Tuple<String, Double>... value) throws IOException {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void commit() throws IOException {
                // TODO Auto-generated method stub
                
            }
        };
    }

}

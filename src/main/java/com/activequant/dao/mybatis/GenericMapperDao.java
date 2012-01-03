package com.activequant.dao.mybatis;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.activequant.dao.mybatis.mapper.GenericRowMapper;
import com.activequant.domainmodel.GenericRow;
import com.activequant.domainmodel.PersistentEntity;

class GenericMapperDao<T extends PersistentEntity> {

    private Logger log = Logger.getLogger(GenericMapperDao.class);
    protected GenericRowMapper mapper;
    private String tableName;
    private Class<? extends PersistentEntity> clazz;

    GenericMapperDao(GenericRowMapper mapper, Class<? extends PersistentEntity> clazz, String table) {
        log.info("Initializing GenericDao for table " + table);
        this.mapper = mapper;
        this.clazz = clazz;
        // don't know how to check with ibatis if a table exists - at least not
        // in an easy way.
        // this one is easier, dirty and safe.
        try {
            mapper.init(table);
        } catch (Exception ex) {
            log.debug("Message while creating table, can be ignored on consecutive runs: " + ex.getStackTrace()[0]);
        }
        this.tableName = table;
        // same story here.
        try {
            mapper.genIndex1(table);
        } catch (Exception ex) {
            log.debug("Index already exists.");
        }
        try {
            mapper.genIndex2(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex3(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex4(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex5(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex6(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex7(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genIndex8(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
        try {
            mapper.genKey9(table);
        } catch (Exception ex) {
        	log.debug("Index already exists.");
        }
    }

    //
    @SuppressWarnings("unchecked")
    public T load(String primaryKey) {

        List<GenericRow> rows = mapper.load(tableName, primaryKey);

        // to be offloaded to a generic class.
        Map<String, Object> map = new HashMap<String, Object>();
        for (GenericRow row : rows) {
            String fieldName = row.getFieldName();
            if (row.getDoubleVal() != null)
                map.put(fieldName, row.getDoubleVal());
            else if (row.getLongVal() != null)
                map.put(fieldName, row.getLongVal());
            else if (row.getStringVal() != null)
                map.put(fieldName, row.getStringVal());
        }
        String className = (String) map.get("ClassName".toUpperCase());
        if (className == null)
            // no valid entry.
            return null;
        T ret = null;
        try {
            @SuppressWarnings({ "rawtypes" })
            Class clazz = Class.forName(className);
            ret = (T) clazz.newInstance();
            ret.initFromMap(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * First sentence: Use with care. This will load ALL object instances from
     * DB, which could possibly become a very large table.
     * 
     * TODO: find another way to load things, this is too slow.
     * 
     * @return array of ALL instances in persistence layer.
     */
    @SuppressWarnings("unchecked")
    public T[] loadAll() {
        List<String> iids = mapper.loadKeyList(tableName);
        T[] ret = (T[]) Array.newInstance(clazz, iids.size());
        for (int i = 0; i < iids.size(); i++) {
            ret[i] = load(iids.get(i));
        }
        return ret;
    }

    public synchronized void delete(T t) {
        mapper.delete(tableName, t.getId());
    }

    public synchronized void update(T t) {
        List<GenericRow> rows = createGenRows(t);
        for (GenericRow row : rows)
            mapper.update(tableName, row);
    }

    /**
     * Use this function to load all IDs
     * 
     * @return a list of all Key-IDs in this set.
     * 
     */
    public String[] loadIDs() {
        List<String> iids = mapper.loadKeyList(tableName);
        return iids.toArray(new String[] {});
    }

    private GenericRow genRow(long createdTimeStamp, String id, String key, Object value) {
        GenericRow gr = null;
        if (value instanceof Double) {
            gr = new GenericRow(createdTimeStamp, id, key, null, (Double) value, null);
        } else if (value instanceof String) {
            gr = new GenericRow(createdTimeStamp, id, key, null, null, (String) value);
        } else if (value instanceof Integer) {
            gr = new GenericRow(createdTimeStamp, id, key, (Long) value, null, null);
        } else if (value instanceof Long) {
            gr = new GenericRow(createdTimeStamp, id, key, (Long) value, null, null);
        }
        return gr;
    }

    private List<GenericRow> createGenRows(T t) {
        List<GenericRow> ret = new ArrayList<GenericRow>();
        // TODO Auto-generated method stub
        Map<String, Object> map = t.propertyMap();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            boolean added = false;
            String key = it.next();
            Object value = map.get(key);
            if (value == null)
                continue;
            GenericRow gr = null;
            long createdTimeStamp = System.currentTimeMillis();
            if (value instanceof Object[]) {
                // generate individual generic rows for all entries of this
                // array.
                Object[] array = (Object[]) value;
                int totalLength = array.length;
                for (int i = 0; i < totalLength; i++) {
                    String fieldName = "[" + key + ";" + i + ";" + totalLength;
                    GenericRow temp = genRow(createdTimeStamp, t.getId(), fieldName, array[i]);
                    ret.add(temp);
                    added = true;
                }
            } else
                gr = genRow(createdTimeStamp, t.getId(), key, value);

            if (gr != null)
                ret.add(gr);
            else if (!added)
                log.warn("NO VALUE CONVERTER FOR VALUE: " + key + "/ " + value);
        }

        return ret;
    }

    public void create(T t) {
        List<GenericRow> rows = createGenRows(t);
        for (GenericRow row : rows)
            mapper.insert(tableName, row);
    }

    public String[] findIDs(String key, String sValue) {
        List<String> ret = mapper.findByString(tableName, key, sValue);
        return ret.toArray(new String[] {});
    }

    public String[] findIDsWhereLongValGreater(String fieldName, long sValue) {
        List<String> ret = mapper.findIDsWhereLongValGreater(tableName, fieldName, sValue);
        return ret.toArray(new String[] {});
    }

    public String[] findIDs(String key, Double dValue) {
        List<String> ret = mapper.findByDouble(tableName, key, dValue);
        return ret.toArray(new String[] {});
    }

    public String[] findIDs(String key, Long lValue) {
        List<String> ret = mapper.findByLong(tableName, key, lValue);
        return ret.toArray(new String[] {});
    }

    public String[] findIDs(int startIndex, int endIndex) {
        List<String> ret = mapper.findIDs(tableName, startIndex, endIndex);
        return ret.toArray(new String[] {});
    }

    public String[] findIdsLike(String idsLikeString, int resultAmount) {
        List<String> ret = mapper.findIdsLike(tableName, idsLikeString, resultAmount);
        return ret.toArray(new String[] {});
    }

    public int countForAttributeValue(String key, String value) {
        return mapper.countForAttributeValue(tableName, key, value);
    }

    public int count() {
        return mapper.count(tableName);
    }

}
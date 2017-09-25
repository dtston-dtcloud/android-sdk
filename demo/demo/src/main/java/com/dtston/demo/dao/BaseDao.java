package com.dtston.demo.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.dtston.demo.db.DataHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

public class BaseDao<T, ID> {
	private DataHelper mHelper;
	public Dao mDao;
	
	protected BaseDao() {
		mHelper = DataHelper.getHelper();
	}

	protected DataHelper getHelper() {
		return mHelper;
	}
	
	public int insert(T table) {
		int id = -1;
		try {
			id = mDao.create(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public int update(T table) {
		int id = -1;
		try {
			id = mDao.update(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public int delete(T table) {
		int id = -1;
		try {
			id = mDao.delete(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	
	protected T queryForFirstWhere(Map<String, Object> where) {
		List<T> result = new ArrayList<T>();
		QueryBuilder queryBuilder = mDao.queryBuilder();
		
		int qbWherePosition = 0;
		Iterator<Entry<String, Object>> iterWhere = where.entrySet().iterator();
		Entry<String, Object> entry;
		Where<T, ID> qbWhere = queryBuilder.where();
		while(iterWhere.hasNext()) {
			entry = iterWhere.next();
			try {
				if (qbWherePosition == 0) {
					qbWhere = qbWhere.eq(entry.getKey(), entry.getValue());
				} else {
					qbWhere = qbWhere.and().eq(entry.getKey(), entry.getValue());
				}
				qbWherePosition++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			queryBuilder.setWhere(qbWhere);
			result = queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (result.size() > 0) {
			return result.get(0);
		}
		
		return null;
	}
	
	protected List<T> queryWhere(Map<String, Object> where) {
		List<T> result = new ArrayList<T>();
		QueryBuilder queryBuilder = mDao.queryBuilder();
		
		int qbWherePosition = 0;
		Iterator<Entry<String, Object>> iterWhere = where.entrySet().iterator();
		Entry<String, Object> entry;
		Where<T, ID> qbWhere = queryBuilder.where();
		while(iterWhere.hasNext()) {
			entry = iterWhere.next();
			try {
				if (qbWherePosition == 0) {
					qbWhere = qbWhere.eq(entry.getKey(), entry.getValue());
				} else {
					qbWhere = qbWhere.and().eq(entry.getKey(), entry.getValue());
				}
				qbWherePosition++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			queryBuilder.setWhere(qbWhere);
			result = queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 更新数据
	 * @param update 更新的字段
	 * @param where 更新的条件
	 * @return
	 */
	protected boolean update(Map<String,Object> update, Map<String,Object> where){
    	UpdateBuilder ub=mDao.updateBuilder();    	
    	if(update!=null && !update.isEmpty() && update.size()>0){
    		try{    			
    			for(Entry<String,Object> entry:update.entrySet()){
        			String key=entry.getKey();
        			Object value=entry.getValue();
        			ub.updateColumnValue(key, value);
        		}
    			
    			if(where!=null && !where.isEmpty() && where.size()>0){
    				Where mWhere=ub.where();
    				mWhere=getWhere(where, mWhere);
    				ub.setWhere(mWhere);
    			}    			
    			int upId=ub.update();
    			if(upId > 0){
    				return true;
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return false;
    }
	
	/**
	 * 查询表的多个字段的and判断
	 * @param where 判断的map键值对
	 * @param mWhere where的判断对象
	 * @return 返回where的对象
	 */	
	protected Where getWhere(Map<String,Object> where,Where mWhere){
		if(where!=null && !where.isEmpty() && where.size()>0){
			try{
				int size=where.size();
				String[] keys=new String[where.size()];
				keys=where.keySet().toArray(keys);
				
				if(size==1){
					mWhere.eq(keys[0], where.get(keys[0]));
				}else if(size==2){					
					mWhere.and(mWhere.eq(keys[0], where.get(keys[0])),mWhere.eq(keys[1], where.get(keys[1])));						
				}else{
					Where[] w=new Where[size-2];
					for(int i=0;i<size-2;i++){
						w[i]=mWhere.eq(keys[i+2], where.get(keys[i+2]));
					}						
					mWhere.and(mWhere.eq(keys[0], where.get(keys[0])),mWhere.eq(keys[1], where.get(keys[1])),w);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return mWhere;
	}
	
	/**
	 * 多表查询
	 * @param sql 查询的sql语句
	 * @param obj 返回的对象(对象的属性要和查询的字段一一对应)
	 * @return 返回list集合
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryMultiTable(String sql,final Class<T> obj){
		List<T> list= new ArrayList<T>();
		try {
			GenericRawResults<T> result=mDao.queryRaw(sql,new RawRowMapper<T>() {
				@Override
				public T mapRow(String[] columnNames, String[] columnValues)throws SQLException {
					// TODO Auto-generated method stub
					Map<String,Object> map=new HashMap<String,Object>();
					for(int i=0;i<columnNames.length;i++){
						map.put(columnNames[i], columnValues[i]);
					}
					String json=JSONObject.toJSONString(map);
					T t=JSONObject.parseObject(json,obj);
					return t;
				}
			});					
			if(result!=null){
				list=result.getResults();
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}

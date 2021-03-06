/*
 * GovPay - Porta di Accesso al Nodo dei Pagamenti SPC 
 * http://www.gov4j.it/govpay
 * 
 * Copyright (c) 2014-2016 Link.it srl (http://www.link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.govpay.orm.dao.jdbc;

import it.govpay.orm.IdPortale;
import it.govpay.orm.Portale;
import it.govpay.orm.PortaleApplicazione;
import it.govpay.orm.dao.jdbc.converter.PortaleFieldConverter;
import it.govpay.orm.dao.jdbc.fetch.PortaleFetch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.InUse;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.Union;
import org.openspcoop2.generic_project.beans.UnionExpression;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceSearchWithId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.IJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.UtilsTemplate;
import org.openspcoop2.utils.sql.ISQLQueryObject;

/**     
 * JDBCPortaleServiceSearchImpl
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPortaleServiceSearchImpl implements IJDBCServiceSearchWithId<Portale, IdPortale, JDBCServiceManager> {

	private PortaleFieldConverter _portaleFieldConverter = null;
	public PortaleFieldConverter getPortaleFieldConverter() {
		if(this._portaleFieldConverter==null){
			this._portaleFieldConverter = new PortaleFieldConverter(this.jdbcServiceManager.getJdbcProperties().getDatabaseType());
		}		
		return this._portaleFieldConverter;
	}
	@Override
	public ISQLFieldConverter getFieldConverter() {
		return this.getPortaleFieldConverter();
	}
	
	private PortaleFetch portaleFetch = new PortaleFetch();
	public PortaleFetch getPortaleFetch() {
		return this.portaleFetch;
	}
	@Override
	public IJDBCFetch getFetch() {
		return getPortaleFetch();
	}
	
	
	private JDBCServiceManager jdbcServiceManager = null;

	@Override
	public void setServiceManager(JDBCServiceManager serviceManager) throws ServiceException{
		this.jdbcServiceManager = serviceManager;
	}
	
	@Override
	public JDBCServiceManager getServiceManager() throws ServiceException{
		return this.jdbcServiceManager;
	}
	

	@Override
	public IdPortale convertToId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Portale portale) throws NotImplementedException, ServiceException, Exception{
	
		IdPortale idPortale = new IdPortale();
		idPortale.setCodPortale(portale.getCodPortale());
	
		return idPortale;
	}
	
	@Override
	public Portale get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {
		Long id_portale = ( (id!=null && id.getId()!=null && id.getId()>0) ? id.getId() : this.findIdPortale(jdbcProperties, log, connection, sqlQueryObject, id, true));
		return this._get(jdbcProperties, log, connection, sqlQueryObject, id_portale,idMappingResolutionBehaviour);
		
		
	}
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id) throws MultipleResultException, NotImplementedException, ServiceException,Exception {

		Long id_portale = this.findIdPortale(jdbcProperties, log, connection, sqlQueryObject, id, false);
		return id_portale != null && id_portale > 0;
		
	}
	
	@Override
	public List<IdPortale> findAllIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		// default behaviour (id-mapping)
        if(idMappingResolutionBehaviour==null){
                idMappingResolutionBehaviour = org.openspcoop2.generic_project.beans.IDMappingBehaviour.valueOf("USE_TABLE_ID");
        }
		List<IdPortale> list = new ArrayList<IdPortale>();

		try{
			List<IField> fields = new ArrayList<IField>();
			fields.add(Portale.model().COD_PORTALE);

			List<Map<String, Object>> returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, fields.toArray(new IField[1]));
        
			for(Map<String, Object> map: returnMap) {
				list.add(this.convertToId(jdbcProperties, log, connection, sqlQueryObject, (Portale)this.getPortaleFetch().fetch(jdbcProperties.getDatabase(), Portale.model(), map)));
	        }
		} catch(NotFoundException e) {}

        return list;

	}
	
	@Override
	public List<Portale> findAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException, ServiceException,Exception {

		// default behaviour (id-mapping)
        if(idMappingResolutionBehaviour==null){
                idMappingResolutionBehaviour = org.openspcoop2.generic_project.beans.IDMappingBehaviour.valueOf("USE_TABLE_ID");
        }
        
        List<Portale> list = new ArrayList<Portale>();

		try{
			List<IField> fields = new ArrayList<IField>();
			fields.add(new CustomField("id", Long.class, "id", this.getPortaleFieldConverter().toTable(Portale.model())));
			fields.add(Portale.model().COD_PORTALE);
			fields.add(Portale.model().DEFAULT_CALLBACK_URL);
			fields.add(Portale.model().PRINCIPAL);
			fields.add(Portale.model().ABILITATO);

			List<Map<String, Object>> returnMap = this.select(jdbcProperties, log, connection, sqlQueryObject, expression, fields.toArray(new IField[1]));

			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		

			for(Map<String, Object> map: returnMap) {
				Portale portale = (Portale)this.getPortaleFetch().fetch(jdbcProperties.getDatabase(), Portale.model(), map);


				// Object portale_portaleApplicazione
				ISQLQueryObject sqlQueryObjectGet_portale_portaleApplicazione = sqlQueryObject.newSQLQueryObject();
				sqlQueryObjectGet_portale_portaleApplicazione.setANDLogicOperator(true);
				sqlQueryObjectGet_portale_portaleApplicazione.addFromTable(this.getPortaleFieldConverter().toTable(Portale.model().PORTALE_APPLICAZIONE));
				sqlQueryObjectGet_portale_portaleApplicazione.addSelectField("id");
				sqlQueryObjectGet_portale_portaleApplicazione.addWhereCondition("id_portale=?");

				// Get portale_portaleApplicazione
				java.util.List<Object> portale_portaleApplicazione_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectGet_portale_portaleApplicazione.createSQLQuery(), jdbcProperties.isShowSql(), Portale.model().PORTALE_APPLICAZIONE, this.getPortaleFetch(),
					new JDBCObject(portale.getId(),Long.class));

				if(portale_portaleApplicazione_list != null) {
					for (Object portale_portaleApplicazione_object: portale_portaleApplicazione_list) {
						PortaleApplicazione portale_portaleApplicazione = (PortaleApplicazione) portale_portaleApplicazione_object;


						if(idMappingResolutionBehaviour==null ||
							(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) || org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour))
						){
							// Object _portale_portaleApplicazione_applicazione (recupero id)
							ISQLQueryObject sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId = sqlQueryObject.newSQLQueryObject();
							sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId.addFromTable(this.getPortaleFieldConverter().toTable(it.govpay.orm.Portale.model().PORTALE_APPLICAZIONE));
							sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId.addSelectField("id_applicazione");
							sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId.addWhereCondition("id=?");
							sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId.setANDLogicOperator(true);
							Long idFK_portale_portaleApplicazione_applicazione = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet_portale_portaleApplicazione_applicazione_readFkId.createSQLQuery(), jdbcProperties.isShowSql(),Long.class,
									new JDBCObject(portale_portaleApplicazione.getId(),Long.class));
							
							it.govpay.orm.IdApplicazione id_portale_portaleApplicazione_applicazione = null;
							if(idMappingResolutionBehaviour==null || org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour)){
								id_portale_portaleApplicazione_applicazione = ((JDBCApplicazioneServiceSearch)(this.getServiceManager().getApplicazioneServiceSearch())).findId(idFK_portale_portaleApplicazione_applicazione, false);
							}else{
								id_portale_portaleApplicazione_applicazione = new it.govpay.orm.IdApplicazione();
							}
							id_portale_portaleApplicazione_applicazione.setId(idFK_portale_portaleApplicazione_applicazione);
							portale_portaleApplicazione.setIdApplicazione(id_portale_portaleApplicazione_applicazione);
						}

						portale.addPortaleApplicazione(portale_portaleApplicazione);
					}
				}

				list.add(portale);
	        }
		} catch(NotFoundException e) {}

        return list;
		
	}
	
	@Override
	public Portale find(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) 
		throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException,Exception {

        long id = this.findTableId(jdbcProperties, log, connection, sqlQueryObject, expression);
        if(id>0){
        	return this.get(jdbcProperties, log, connection, sqlQueryObject, id, idMappingResolutionBehaviour);
        }else{
        	throw new NotFoundException("Entry with id["+id+"] not found");
        }
		
	}
	
	@Override
	public NonNegativeNumber count(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareCount(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaleFieldConverter(), Portale.model());
		
		sqlQueryObject.addSelectCountField(this.getPortaleFieldConverter().toTable(Portale.model())+".id","tot",true);
		
		_join(expression,sqlQueryObject);
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.count(jdbcProperties, log, connection, sqlQueryObject, expression,
																			this.getPortaleFieldConverter(), Portale.model(),listaQuery);
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id) throws NotFoundException, NotImplementedException, ServiceException,Exception {
		
		Long id_portale = this.findIdPortale(jdbcProperties, log, connection, sqlQueryObject, id, true);
        return this._inUse(jdbcProperties, log, connection, sqlQueryObject, id_portale);
		
	}

	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Object> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		List<Map<String,Object>> map = 
			this.select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, distinct, new IField[]{field});
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectSingleObject(map);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return this.select(jdbcProperties, log, connection, sqlQueryObject,
								paginatedExpression, false, field);
	}
	
	@Override
	public List<Map<String,Object>> select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, boolean distinct, IField ... field) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,field);
		try{
		
			ISQLQueryObject sqlQueryObjectDistinct = 
						org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(distinct,sqlQueryObject, paginatedExpression, log,
												this.getPortaleFieldConverter(), field);

			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression, sqlQueryObjectDistinct);
			
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,field);
		}
	}

	@Override
	public Object aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		Map<String,Object> map = 
			this.aggregate(jdbcProperties, log, connection, sqlQueryObject, expression, new FunctionField[]{functionField});
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.selectAggregateObject(map,functionField);
	}
	
	@Override
	public Map<String,Object> aggregate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {													
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			List<Map<String,Object>> list = _select(jdbcProperties, log, connection, sqlQueryObject, expression);
			return list.get(0);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCExpression expression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(expression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,expression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, expression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,expression,functionField);
		}
	}
	

	@Override
	public List<Map<String,Object>> groupBy(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
													JDBCPaginatedExpression paginatedExpression, FunctionField ... functionField) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		if(paginatedExpression.getGroupByFields().size()<=0){
			throw new ServiceException("GroupBy conditions not found in expression");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.setFields(sqlQueryObject,paginatedExpression,functionField);
		try{
			return _select(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression);
		}finally{
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.removeFields(sqlQueryObject,paginatedExpression,functionField);
		}
	}
	
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		return _select(jdbcProperties, log, connection, sqlQueryObject, expression, null);
	}
	protected List<Map<String,Object>> _select(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												IExpression expression, ISQLQueryObject sqlQueryObjectDistinct) throws ServiceException,NotFoundException,NotImplementedException,Exception {
		
		List<Object> listaQuery = new ArrayList<Object>();
		List<JDBCObject> listaParams = new ArrayList<JDBCObject>();
		List<Object> returnField = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSelect(jdbcProperties, log, connection, sqlQueryObject, 
        						expression, this.getPortaleFieldConverter(), Portale.model(), 
        						listaQuery,listaParams);
		
		_join(expression,sqlQueryObject);
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.select(jdbcProperties, log, connection,
        								org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareSqlQueryObjectForSelectDistinct(sqlQueryObject,sqlQueryObjectDistinct), 
        								expression, this.getPortaleFieldConverter(), Portale.model(),
        								listaQuery,listaParams,returnField);
		if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}
	
	@Override
	public List<Map<String,Object>> union(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnion(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getPortaleFieldConverter(), Portale.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        List<Map<String,Object>> list = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.union(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaleFieldConverter(), Portale.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(list!=null && list.size()>0){
			return list;
		}
		else{
			throw new NotFoundException("Not Found");
		}								
	}
	
	@Override
	public NonNegativeNumber unionCount(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
												Union union, UnionExpression ... unionExpression) throws ServiceException,NotFoundException,NotImplementedException,Exception {		
		
		List<ISQLQueryObject> sqlQueryObjectInnerList = new ArrayList<ISQLQueryObject>();
		List<JDBCObject> jdbcObjects = new ArrayList<JDBCObject>();
		List<Class<?>> returnClassTypes = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareUnionCount(jdbcProperties, log, connection, sqlQueryObject, 
        						this.getPortaleFieldConverter(), Portale.model(), 
        						sqlQueryObjectInnerList, jdbcObjects, union, unionExpression);
		
		if(unionExpression!=null){
			for (int i = 0; i < unionExpression.length; i++) {
				UnionExpression ue = unionExpression[i];
				IExpression expression = ue.getExpression();
				_join(expression,sqlQueryObjectInnerList.get(i));
			}
		}
        
        NonNegativeNumber number = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.unionCount(jdbcProperties, log, connection, sqlQueryObject, 
        								this.getPortaleFieldConverter(), Portale.model(), 
        								sqlQueryObjectInnerList, jdbcObjects, returnClassTypes, union, unionExpression);
        if(number!=null && number.longValue()>=0){
			return number;
		}
		else{
			throw new NotFoundException("Not Found");
		}
	}



	// -- ConstructorExpression	

	@Override
	public JDBCExpression newExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(this.getPortaleFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}


	@Override
	public JDBCPaginatedExpression newPaginatedExpression(Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(this.getPortaleFieldConverter());
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	@Override
	public JDBCExpression toExpression(JDBCPaginatedExpression paginatedExpression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCExpression(paginatedExpression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}

	@Override
	public JDBCPaginatedExpression toPaginatedExpression(JDBCExpression expression, Logger log) throws NotImplementedException, ServiceException {
		try{
			return new JDBCPaginatedExpression(expression);
		}catch(Exception e){
			throw new ServiceException(e);
		}
	}
	
	
	
	// -- DB

	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id, Portale obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,id,null));
	}
	
	@Override
	public void mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Portale obj) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		_mappingTableIds(jdbcProperties,log,connection,sqlQueryObject,obj,
				this.get(jdbcProperties,log,connection,sqlQueryObject,tableId,null));
	}
	private void _mappingTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Portale obj, Portale imgSaved) throws NotFoundException,NotImplementedException,ServiceException,Exception{
		if(imgSaved==null){
			return;
		}
		obj.setId(imgSaved.getId());
		if(obj.getPortaleApplicazioneList()!=null){
			List<it.govpay.orm.PortaleApplicazione> listObj_ = obj.getPortaleApplicazioneList();
			for(it.govpay.orm.PortaleApplicazione itemObj_ : listObj_){
				it.govpay.orm.PortaleApplicazione itemAlreadySaved_ = null;
				if(imgSaved.getPortaleApplicazioneList()!=null){
					List<it.govpay.orm.PortaleApplicazione> listImgSaved_ = imgSaved.getPortaleApplicazioneList();
					for(it.govpay.orm.PortaleApplicazione itemImgSaved_ : listImgSaved_){
						boolean objEqualsToImgSaved_ = false;
						objEqualsToImgSaved_ = org.openspcoop2.generic_project.utils.Utilities.equals(itemObj_.getIdApplicazione(),itemImgSaved_.getIdApplicazione());
						if(objEqualsToImgSaved_){
							itemAlreadySaved_=itemImgSaved_;
							break;
						}
					}
				}
				if(itemAlreadySaved_!=null){
					itemObj_.setId(itemAlreadySaved_.getId());
					if(itemObj_.getIdApplicazione()!=null && 
							itemAlreadySaved_.getIdApplicazione()!=null){
						itemObj_.getIdApplicazione().setId(itemAlreadySaved_.getIdApplicazione().getId());
					}
				}
			}
		}

	}
	
	@Override
	public Portale get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._get(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId), idMappingResolutionBehaviour);
	}
	
	private Portale _get(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		IField idField = new CustomField("id", Long.class, "id", this.getPortaleFieldConverter().toTable(Portale.model()));
		JDBCPaginatedExpression expression = this.newPaginatedExpression(log);
		
		expression.equals(idField, tableId);
		expression.offset(0);
		expression.limit(2); //per verificare la multiple results
		expression.addOrder(idField, org.openspcoop2.generic_project.expression.SortOrder.ASC);
		List<Portale> lst = this.findAll(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), expression, idMappingResolutionBehaviour);
		
		if(lst.size() <=0)
			throw new NotFoundException("Id ["+tableId+"]");
				
		if(lst.size() > 1)
			throw new MultipleResultException("Id ["+tableId+"]");
		

		return lst.get(0);
	
	} 
	
	@Override
	public boolean exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
		return this._exists(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
	
	private boolean _exists(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws MultipleResultException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
				
		boolean existsPortale = false;

		sqlQueryObject = sqlQueryObject.newSQLQueryObject();
		sqlQueryObject.setANDLogicOperator(true);

		sqlQueryObject.addFromTable(this.getPortaleFieldConverter().toTable(Portale.model()));
		sqlQueryObject.addSelectField(this.getPortaleFieldConverter().toColumn(Portale.model().COD_PORTALE,true));
		sqlQueryObject.addWhereCondition("id=?");


		// Exists portale
		existsPortale = jdbcUtilities.exists(sqlQueryObject.createSQLQuery(), jdbcProperties.isShowSql(),
			new JDBCObject(tableId,Long.class));

		
        return existsPortale;
	
	}
	
	private void _join(IExpression expression, ISQLQueryObject sqlQueryObject) throws NotImplementedException, ServiceException, Exception{
	
		if(expression.inUseModel(Portale.model().PORTALE_APPLICAZIONE,false)){
			String tableName1 = this.getPortaleFieldConverter().toAliasTable(Portale.model());
			String tableName2 = this.getPortaleFieldConverter().toAliasTable(Portale.model().PORTALE_APPLICAZIONE);
			sqlQueryObject.addWhereCondition(tableName1+".id="+tableName2+".id_portale");
			
	        if(expression.inUseModel(Portale.model().PORTALE_APPLICAZIONE.ID_APPLICAZIONE,false)){
				if(expression.inUseModel(Portale.model().PORTALE_APPLICAZIONE,false)==false){
					sqlQueryObject.addFromTable(this.getPortaleFieldConverter().toTable(Portale.model().PORTALE_APPLICAZIONE));
				}
				
				String tableName3 = this.getPortaleFieldConverter().toAliasTable(Portale.model().PORTALE_APPLICAZIONE.ID_APPLICAZIONE);
				sqlQueryObject.addWhereCondition(tableName3+".id="+tableName2+".id_applicazione");

			}

		}
        
	}
	
	protected java.util.List<Object> _getRootTablePrimaryKeyValues(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id) throws NotFoundException, ServiceException, NotImplementedException, Exception{
	    // Identificativi
        java.util.List<Object> rootTableIdValues = new java.util.ArrayList<Object>();
		Long longId = this.findIdPortale(jdbcProperties, log, connection, sqlQueryObject.newSQLQueryObject(), id, true);
		rootTableIdValues.add(longId);
        
        return rootTableIdValues;
	}
	
	protected Map<String, List<IField>> _getMapTableToPKColumn() throws NotImplementedException, Exception{
	
		PortaleFieldConverter converter = this.getPortaleFieldConverter();
		Map<String, List<IField>> mapTableToPKColumn = new java.util.Hashtable<String, List<IField>>();
		UtilsTemplate<IField> utilities = new UtilsTemplate<IField>();

		//		  If a table doesn't have a primary key, don't add it to this map

		// Portale.model()
		mapTableToPKColumn.put(converter.toTable(Portale.model()),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Portale.model()))
			));

		// Portale.model().PORTALE_APPLICAZIONE
		mapTableToPKColumn.put(converter.toTable(Portale.model().PORTALE_APPLICAZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Portale.model().PORTALE_APPLICAZIONE))
			));

		// Portale.model().PORTALE_APPLICAZIONE.ID_APPLICAZIONE
		mapTableToPKColumn.put(converter.toTable(Portale.model().PORTALE_APPLICAZIONE.ID_APPLICAZIONE),
			utilities.newList(
				new CustomField("id", Long.class, "id", converter.toTable(Portale.model().PORTALE_APPLICAZIONE.ID_APPLICAZIONE))
			));

        return mapTableToPKColumn;		
	}
	
	@Override
	public List<Long> findAllTableIds(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCPaginatedExpression paginatedExpression) throws ServiceException, NotImplementedException, Exception {
		
		List<Long> list = new ArrayList<Long>();

		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaleFieldConverter().toTable(Portale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFindAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
												this.getPortaleFieldConverter(), Portale.model());
		
		_join(paginatedExpression,sqlQueryObject);
		
		List<Object> listObjects = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.findAll(jdbcProperties, log, connection, sqlQueryObject, paginatedExpression,
																			this.getPortaleFieldConverter(), Portale.model(), objectIdClass, listaQuery);
		for(Object object: listObjects) {
			list.add((Long)object);
		}

        return list;
		
	}
	
	@Override
	public long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws ServiceException, NotFoundException, MultipleResultException, NotImplementedException, Exception {
	
		sqlQueryObject.setSelectDistinct(true);
		sqlQueryObject.setANDLogicOperator(true);
		sqlQueryObject.addSelectField(this.getPortaleFieldConverter().toTable(Portale.model())+".id");
		Class<?> objectIdClass = Long.class;
		
		List<Object> listaQuery = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.prepareFind(jdbcProperties, log, connection, sqlQueryObject, expression,
												this.getPortaleFieldConverter(), Portale.model());
		
		_join(expression,sqlQueryObject);

		Object res = org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.find(jdbcProperties, log, connection, sqlQueryObject, expression,
														this.getPortaleFieldConverter(), Portale.model(), objectIdClass, listaQuery);
		if(res!=null && (((Long) res).longValue()>0) ){
			return ((Long) res).longValue();
		}
		else{
			throw new NotFoundException("Not Found");
		}
		
	}

	@Override
	public InUse inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {
		return this._inUse(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}

	private InUse _inUse(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long tableId) throws ServiceException, NotFoundException, NotImplementedException, Exception {

		InUse inUse = new InUse();
		inUse.setInUse(false);
		
		// Delete this line when you have implemented the method
		int throwNotImplemented = 1;
		if(throwNotImplemented==1){
		        throw new NotImplementedException("NotImplemented");
		}
		// Delete this line when you have implemented the method

        return inUse;

	}
	
	@Override
	public IdPortale findId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _portale
		sqlQueryObjectGet.addFromTable(this.getPortaleFieldConverter().toTable(Portale.model()));
		sqlQueryObjectGet.addSelectField(this.getPortaleFieldConverter().toColumn(Portale.model().COD_PORTALE,true));

		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.addWhereCondition("id=?");

		// Recupero _portale
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portale = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(tableId,Long.class)
		};
		List<Class<?>> listaFieldIdReturnType_portale = new ArrayList<Class<?>>();
		listaFieldIdReturnType_portale.add(Portale.model().COD_PORTALE.getFieldType());

		it.govpay.orm.IdPortale id_portale = null;
		List<Object> listaFieldId_portale = jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
				listaFieldIdReturnType_portale, searchParams_portale);
		if(listaFieldId_portale==null || listaFieldId_portale.size()<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		else{
			// set _portale
			id_portale = new it.govpay.orm.IdPortale();
			id_portale.setCodPortale((String)listaFieldId_portale.get(0));
		}
		
		return id_portale;
		
	}

	@Override
	public Long findTableId(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id, boolean throwNotFound)
			throws NotFoundException, ServiceException, NotImplementedException, Exception {
	
		return this.findIdPortale(jdbcProperties,log,connection,sqlQueryObject,id,throwNotFound);
			
	}
	
	@Override
	public List<List<Object>> nativeQuery(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, 
											String sql,List<Class<?>> returnClassTypes,Object ... param) throws ServiceException,NotFoundException,NotImplementedException,Exception{
		
		return org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities.nativeQuery(jdbcProperties, log, connection, sqlQueryObject,
																							sql,returnClassTypes,param);
														
	}
	
	protected Long findIdPortale(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPortale id, boolean throwNotFound) throws NotFoundException, ServiceException, NotImplementedException, Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);

		ISQLQueryObject sqlQueryObjectGet = sqlQueryObject.newSQLQueryObject();

		// Object _portale
		sqlQueryObjectGet.addFromTable(this.getPortaleFieldConverter().toTable(Portale.model()));
		sqlQueryObjectGet.addSelectField("id");
		sqlQueryObjectGet.setANDLogicOperator(true);
		sqlQueryObjectGet.setSelectDistinct(true);
		sqlQueryObjectGet.addWhereCondition(this.getPortaleFieldConverter().toColumn(Portale.model().COD_PORTALE,true)+"=?");

		// Recupero _portale
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_portale = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] { 
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id.getCodPortale(),Portale.model().COD_PORTALE.getFieldType()),
		};
		Long id_portale = null;
		try{
			id_portale = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectGet.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_portale);
		}catch(NotFoundException notFound){
			if(throwNotFound){
				throw new NotFoundException(notFound);
			}
		}
		if(id_portale==null || id_portale<=0){
			if(throwNotFound){
				throw new NotFoundException("Not Found");
			}
		}
		
		return id_portale;
	}
}

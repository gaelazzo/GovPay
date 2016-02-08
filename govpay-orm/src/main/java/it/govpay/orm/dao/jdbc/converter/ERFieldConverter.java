/*
 * GovPay - Porta di Accesso al Nodo dei Pagamenti SPC 
 * http://www.gov4j.it/govpay
 * 
 * Copyright (c) 2014-2015 Link.it srl (http://www.link.it).
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
package it.govpay.orm.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import it.govpay.orm.ER;


/**     
 * ERFieldConverter
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ERFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ERFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ERFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ER.model();
	}
	
	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return this.databaseType;
	}
	


	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		// In the case of columns with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the column containing the alias
		
		if(field.equals(ER.model().ID_RR.COD_MSG_REVOCA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cod_msg_revoca";
			}else{
				return "cod_msg_revoca";
			}
		}
		if(field.equals(ER.model().ID_TRACCIATO_XML.ID_TRACCIATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_tracciato";
			}else{
				return "id_tracciato";
			}
		}
		if(field.equals(ER.model().COD_MSG_ESITO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cod_msg_esito";
			}else{
				return "cod_msg_esito";
			}
		}
		if(field.equals(ER.model().DATA_ORA_MSG_ESITO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ora_msg_esito";
			}else{
				return "data_ora_msg_esito";
			}
		}
		if(field.equals(ER.model().IMPORTO_TOTALE_REVOCATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".importo_totale_revocato";
			}else{
				return "importo_totale_revocato";
			}
		}
		if(field.equals(ER.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(ER.model().DESCRIZIONE_STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".descrizione_stato";
			}else{
				return "descrizione_stato";
			}
		}
		if(field.equals(ER.model().DATA_ORA_CREAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ora_creazione";
			}else{
				return "data_ora_creazione";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ER.model().ID_RR.COD_MSG_REVOCA)){
			return this.toTable(ER.model().ID_RR, returnAlias);
		}
		if(field.equals(ER.model().ID_TRACCIATO_XML.ID_TRACCIATO)){
			return this.toTable(ER.model().ID_TRACCIATO_XML, returnAlias);
		}
		if(field.equals(ER.model().COD_MSG_ESITO)){
			return this.toTable(ER.model(), returnAlias);
		}
		if(field.equals(ER.model().DATA_ORA_MSG_ESITO)){
			return this.toTable(ER.model(), returnAlias);
		}
		if(field.equals(ER.model().IMPORTO_TOTALE_REVOCATO)){
			return this.toTable(ER.model(), returnAlias);
		}
		if(field.equals(ER.model().STATO)){
			return this.toTable(ER.model(), returnAlias);
		}
		if(field.equals(ER.model().DESCRIZIONE_STATO)){
			return this.toTable(ER.model(), returnAlias);
		}
		if(field.equals(ER.model().DATA_ORA_CREAZIONE)){
			return this.toTable(ER.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ER.model())){
			return "er";
		}
		if(model.equals(ER.model().ID_RR)){
			return "rr";
		}
		if(model.equals(ER.model().ID_TRACCIATO_XML)){
			return "tracciatixml";
		}


		return super.toTable(model,returnAlias);
		
	}

}
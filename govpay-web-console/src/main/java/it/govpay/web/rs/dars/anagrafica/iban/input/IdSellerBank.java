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
package it.govpay.web.rs.dars.anagrafica.iban.input;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import it.govpay.web.rs.dars.model.RawParamValue;
import it.govpay.web.rs.dars.model.input.dinamic.InputText;
import it.govpay.web.utils.Utils;

public class IdSellerBank  extends InputText{
	
	private String attivatoObepId= null;
	private String nomeServizio = null;

	public IdSellerBank(String nomeServizio,String id, String label, int minLength, int maxLength, URI refreshUri,	List<RawParamValue> values, Object... objects) {
		super(id, label, minLength, maxLength, refreshUri, values);
		this.nomeServizio = nomeServizio;
		this.attivatoObepId = Utils.getInstance().getMessageFromResourceBundle(this.nomeServizio +  ".attivatoObep.id");
	}

	@Override
	protected String getDefaultValue(List<RawParamValue> values, Object... objects) {
		String attivatoObepValue = Utils.getValue(values, this.attivatoObepId);
		
		if(StringUtils.isNotEmpty(attivatoObepValue) && Boolean.parseBoolean(attivatoObepValue)){
			return "";
		}
		
		return null;
	}

	@Override
	protected boolean isRequired(List<RawParamValue> values, Object... objects) {
		String attivatoObepValue = Utils.getValue(values, this.attivatoObepId);
		
		if(StringUtils.isNotEmpty(attivatoObepValue) && Boolean.parseBoolean(attivatoObepValue)){
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean isHidden(List<RawParamValue> values, Object... objects) {
		String attivatoObepValue = Utils.getValue(values, this.attivatoObepId);
		
		if(StringUtils.isNotEmpty(attivatoObepValue) && Boolean.parseBoolean(attivatoObepValue)){
			return false;
		}
		
		return true;
	}

	@Override
	protected boolean isEditable(List<RawParamValue> values, Object... objects) {
		String attivatoObepValue = Utils.getValue(values, this.attivatoObepId);
		
		if(StringUtils.isNotEmpty(attivatoObepValue) && Boolean.parseBoolean(attivatoObepValue)){
			return true;
		}
		
		return false;
	}

}

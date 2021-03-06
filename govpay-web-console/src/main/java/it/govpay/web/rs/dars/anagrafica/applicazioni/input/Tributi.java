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
package it.govpay.web.rs.dars.anagrafica.applicazioni.input;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.SortOrder;

import it.govpay.bd.BasicBD;
import it.govpay.bd.FilterSortWrapper;
import it.govpay.bd.anagrafica.ApplicazioniBD;
import it.govpay.bd.anagrafica.DominiBD;
import it.govpay.bd.anagrafica.TributiBD;
import it.govpay.bd.anagrafica.filters.TributoFilter;
import it.govpay.bd.model.Applicazione;
import it.govpay.bd.model.Tributo;
import it.govpay.web.rs.dars.anagrafica.tributi.TributiHandler;
import it.govpay.web.rs.dars.model.Elemento;
import it.govpay.web.rs.dars.model.RawParamValue;
import it.govpay.web.rs.dars.model.Voce;
import it.govpay.web.rs.dars.model.input.dinamic.MultiSelectList;
import it.govpay.web.utils.Utils;

public class Tributi extends MultiSelectList<Long, List<Long>>{

	private String trustedId= null;
	private String applicazioneId = null;
	private String nomeServizio = null;

	public Tributi(String nomeServizio,String id, String label, URI refreshUri, List<RawParamValue> paramValues,
			 Object... objects) {
		super(id, label, refreshUri, paramValues, objects);
		this.nomeServizio = nomeServizio;
		this.trustedId = Utils.getInstance().getMessageFromResourceBundle(this.nomeServizio + ".trusted.id");
		this.applicazioneId = Utils.getInstance().getMessageFromResourceBundle(this.nomeServizio + ".id.id");
		this.log = LogManager.getLogger();
	}

	@Override
	protected List<Voce<Long>> getValues(List<RawParamValue> paramValues, Object... objects) throws ServiceException {
		String trustedValue = Utils.getValue(paramValues, this.trustedId);
		List<Voce<Long>> lst = new ArrayList<Voce<Long>>();

		if(StringUtils.isNotEmpty(trustedValue) && trustedValue.equalsIgnoreCase("true")){
			return lst;
		}

		try {
			BasicBD bd = (BasicBD) objects[0];
			TributiBD tributiBD = new TributiBD(bd); 
			DominiBD dominiBD = new DominiBD(bd);

			TributoFilter filter = tributiBD.newFilter();
			FilterSortWrapper fsw = new FilterSortWrapper();
			fsw.setField(it.govpay.orm.Tributo.model().COD_TRIBUTO);
			fsw.setSortOrder(SortOrder.ASC);
			filter.getFilterSortList().add(fsw);
			List<Tributo> findAll = tributiBD.findAll(filter);

			it.govpay.web.rs.dars.anagrafica.tributi.Tributi tributiDars = new it.govpay.web.rs.dars.anagrafica.tributi.Tributi();
			TributiHandler tributiHandler = (TributiHandler) tributiDars.getDarsHandler();
			
			for(Tributo tributo : findAll) {
				Elemento elemento = tributiHandler.getElemento(tributo, tributo.getId(), null, bd);
				lst.add(new Voce<Long>(elemento.getTitolo() + 
						", Dominio: " + dominiBD.getDominio(tributo.getIdDominio()).getCodDominio(), tributo.getId()));
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new ServiceException(e);
		}

		return lst;
	}

	@Override
	protected List<Long> getDefaultValue(List<RawParamValue> values, Object... objects) {
		String trustedValue = Utils.getValue(values, this.trustedId);
		String idapplicazione = Utils.getValue(values, this.applicazioneId);
		List<Long> lst = new ArrayList<Long>();

		if(StringUtils.isNotEmpty(trustedValue) && trustedValue.equalsIgnoreCase("true")){
			return lst;
		}
		if(StringUtils.isEmpty(idapplicazione)){
			return lst;
		}

		try {
			BasicBD bd = (BasicBD) objects[0];
			ApplicazioniBD applicazioniBD = new ApplicazioniBD(bd);
			Applicazione applicazione = applicazioniBD.getApplicazione(Long.parseLong(idapplicazione));
			lst.addAll(applicazione.getIdTributi());
		} catch (Exception e) {
			log.error(e.getMessage(),e); 
		}

		return lst;
	}
	@Override
	protected boolean isRequired(List<RawParamValue> values, Object... objects) {
		String trustedValue = Utils.getValue(values, this.trustedId);

		if(StringUtils.isNotEmpty(trustedValue) && trustedValue.equalsIgnoreCase("true")){
			return false;
		}

		return true;
	}
	@Override
	protected boolean isHidden(List<RawParamValue> values, Object... objects) {
		String trustedValue = Utils.getValue(values, this.trustedId);
		if(StringUtils.isNotEmpty(trustedValue) && trustedValue.equalsIgnoreCase("true")){
			return true;
		}

		return false;
	}
	@Override
	protected boolean isEditable(List<RawParamValue> values, Object... objects) {
		String trustedValue = Utils.getValue(values, this.trustedId);
		if(StringUtils.isNotEmpty(trustedValue) && trustedValue.equalsIgnoreCase("true")){
			return false;
		}
		return true;
	}

}

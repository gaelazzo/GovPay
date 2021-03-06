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
package it.govpay.web.rs.dars.model;

import java.net.URI;

public class VoceRiferimento<T> extends Voce<T>{

	private URI riferimento;
	
	public VoceRiferimento(String etichetta, T valore, URI riferimento) {
		this(etichetta, valore, false, riferimento);
	}
	
	public VoceRiferimento(String etichetta, T valore, boolean avanzata, URI riferimento) {
		super(etichetta, valore,avanzata);
		this.setRiferimento(riferimento);
	}

	public URI getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(URI riferimento) {
		this.riferimento = riferimento;
	}

}


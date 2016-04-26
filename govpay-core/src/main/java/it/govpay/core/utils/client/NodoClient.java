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
package it.govpay.core.utils.client;

import gov.telematici.pagamenti.ws.ppthead.IntestazioneCarrelloPPT;
import gov.telematici.pagamenti.ws.ppthead.IntestazionePPT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediCopiaRT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediCopiaRTRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediElencoFlussiRendicontazione;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediElencoFlussiRendicontazioneRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediFlussoRendicontazione;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediFlussoRendicontazioneRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediInformativaPSP;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediInformativaPSPRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediListaPendentiRPT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediListaPendentiRPTRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediSceltaWISP;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediSceltaWISPRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediStatoRPT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoChiediStatoRPTRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaCarrelloRPT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaCarrelloRPTRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaRPT;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaRPTRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaRichiestaStorno;
import it.gov.digitpa.schemas._2011.ws.paa.NodoInviaRichiestaStornoRisposta;
import it.gov.digitpa.schemas._2011.ws.paa.ObjectFactory;
import it.gov.digitpa.schemas._2011.ws.paa.Risposta;
import it.govpay.bd.model.Intermediario;
import it.govpay.bd.model.Rpt;
import it.govpay.bd.model.Stazione;
import it.govpay.core.exceptions.GovPayException;

import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodoClient extends BasicClient {

	private static ObjectFactory objectFactory;
	private boolean isAzioneInUrl;
	private static Logger log = LogManager.getLogger();

	public NodoClient(Intermediario intermediario) throws ClientException {
		super(intermediario);

		if(objectFactory == null || log == null ){
			objectFactory = new ObjectFactory();
		}
	}

	public Risposta send(String azione, JAXBElement<?> body, Object header) throws GovPayException, ClientException {
		byte[] response = super.sendSoap(azione, body, header, isAzioneInUrl);
		try {
			if(response == null) {
				throw new ClientException("Il Nodo dei Pagamenti ha ritornato un messaggio vuoto.");
			}
			JAXBElement<?> jaxbElement = SOAPUtils.toJaxb(response);
			return (Risposta) jaxbElement.getValue();
		} catch (Exception e) {
			throw new ClientException("Messaggio di risposta dal Nodo dei Pagamenti non valido", e);
		}
	}

	public NodoInviaRPTRisposta nodoInviaRPT(Intermediario intermediario, Stazione stazione, Rpt rpt, NodoInviaRPT inviaRPT) throws GovPayException, ClientException {
		IntestazionePPT intestazione = new IntestazionePPT();
		intestazione.setCodiceContestoPagamento(rpt.getCcp());
		intestazione.setIdentificativoDominio(rpt.getCodDominio());
		intestazione.setIdentificativoIntermediarioPA(intermediario.getCodIntermediario());
		intestazione.setIdentificativoStazioneIntermediarioPA(stazione.getCodStazione());
		intestazione.setIdentificativoUnivocoVersamento(rpt.getIuv());

		Risposta response = send("nodoInviaRPT", objectFactory.createNodoInviaRPT(inviaRPT), intestazione);
		return (NodoInviaRPTRisposta) response;
	}

	public NodoInviaCarrelloRPTRisposta nodoInviaCarrelloRPT(Intermediario intermediario, Stazione stazione, NodoInviaCarrelloRPT inviaCarrelloRPT, String codCarrello) throws GovPayException, ClientException {
		IntestazioneCarrelloPPT intestazione = new IntestazioneCarrelloPPT();
		intestazione.setIdentificativoIntermediarioPA(intermediario.getCodIntermediario());
		intestazione.setIdentificativoStazioneIntermediarioPA(stazione.getCodStazione());
		intestazione.setIdentificativoCarrello(codCarrello);
		Risposta response = send("nodoInviaCarrelloRPT", objectFactory.createNodoInviaCarrelloRPT(inviaCarrelloRPT), intestazione);
		return (NodoInviaCarrelloRPTRisposta) response;
	}

	public NodoChiediInformativaPSPRisposta nodoChiediInformativaPSP(NodoChiediInformativaPSP nodoChiediInformativaPSP, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediInformativaPSP", objectFactory.createNodoChiediInformativaPSP(nodoChiediInformativaPSP), null);
		return (NodoChiediInformativaPSPRisposta) response;
	}

	public NodoChiediStatoRPTRisposta nodoChiediStatoRpt(NodoChiediStatoRPT nodoChiediStatoRPT, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediStatoRPT", objectFactory.createNodoChiediStatoRPT(nodoChiediStatoRPT), null);
		return (NodoChiediStatoRPTRisposta) response;
	}

	public NodoChiediCopiaRTRisposta nodoChiediCopiaRT(NodoChiediCopiaRT nodoChiediCopiaRT, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediCopiaRT", objectFactory.createNodoChiediCopiaRT(nodoChiediCopiaRT), null);
		return (NodoChiediCopiaRTRisposta) response;
	}

	public NodoChiediListaPendentiRPTRisposta nodoChiediListaPendentiRPT(NodoChiediListaPendentiRPT nodoChiediListaPendentiRPT, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediListaPendentiRPT", objectFactory.createNodoChiediListaPendentiRPT(nodoChiediListaPendentiRPT), null);
		return (NodoChiediListaPendentiRPTRisposta) response;
	}

	public NodoInviaRichiestaStornoRisposta nodoInviaRichiestaStorno(NodoInviaRichiestaStorno nodoInviaRichiestaStorno) throws GovPayException, ClientException {
		Risposta response = send("nodoInviaRichiestaStorno", objectFactory.createNodoInviaRichiestaStorno(nodoInviaRichiestaStorno), null);
		return (NodoInviaRichiestaStornoRisposta) response;
	}

	public NodoChiediElencoFlussiRendicontazioneRisposta nodoChiediElencoFlussiRendicontazione(NodoChiediElencoFlussiRendicontazione nodoChiediElencoFlussiRendicontazione, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediElencoFlussiRendicontazione", objectFactory.createNodoChiediElencoFlussiRendicontazione(nodoChiediElencoFlussiRendicontazione), null);
		return (NodoChiediElencoFlussiRendicontazioneRisposta) response;
	}

	public NodoChiediFlussoRendicontazioneRisposta nodoChiediFlussoRendicontazione(NodoChiediFlussoRendicontazione nodoChiediFlussoRendicontazione, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediFlussoRendicontazione", objectFactory.createNodoChiediFlussoRendicontazione(nodoChiediFlussoRendicontazione), null);
		return (NodoChiediFlussoRendicontazioneRisposta) response;
	}

	public NodoChiediSceltaWISPRisposta nodoChiediSceltaWISP(NodoChiediSceltaWISP nodoChiediSceltaWISP, String nomeSoggetto) throws GovPayException, ClientException {
		Risposta response = send("nodoChiediSceltaWISP", objectFactory.createNodoChiediSceltaWISP(nodoChiediSceltaWISP), null);
		return (NodoChiediSceltaWISPRisposta) response;
	}
}
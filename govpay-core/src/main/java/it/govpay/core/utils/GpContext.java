package it.govpay.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;

import org.apache.logging.log4j.LogManager;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.ILogger;
import org.openspcoop2.utils.logger.LoggerFactory;
import org.openspcoop2.utils.logger.beans.Message;
import org.openspcoop2.utils.logger.beans.proxy.Actor;
import org.openspcoop2.utils.logger.beans.proxy.Client;
import org.openspcoop2.utils.logger.beans.proxy.Operation;
import org.openspcoop2.utils.logger.beans.proxy.ProxyContext;
import org.openspcoop2.utils.logger.beans.proxy.Request;
import org.openspcoop2.utils.logger.beans.proxy.Role;
import org.openspcoop2.utils.logger.beans.proxy.Server;
import org.openspcoop2.utils.logger.beans.proxy.Service;
import org.openspcoop2.utils.logger.beans.proxy.Transaction;
import org.openspcoop2.utils.logger.constants.proxy.FlowMode;
import org.openspcoop2.utils.logger.constants.proxy.Result;

import it.gov.spcoop.nodopagamentispc.servizi.pagamentitelematicirpt.PagamentiTelematiciRPTservice;
import it.govpay.bd.model.Intermediario;
import it.govpay.bd.model.Rpt;
import it.govpay.core.utils.client.NodoClient.Azione;
import it.govpay.servizi.commons.GpResponse;

public class GpContext {

	private List<ILogger> loggers;
	private List<Context> contexts;
	
	public GpContext(MessageContext msgCtx) throws ServiceException {
		try {
			loggers = new ArrayList<ILogger>();
			ILogger logger = LoggerFactory.newLogger(new Context());	
			loggers.add(logger);
			
			contexts = new ArrayList<Context>();
			Context context = (Context) logger.getContext();
			contexts.add(context);
			
			Transaction transaction = context.getTransaction();
			transaction.setRole(Role.SERVER);
			
			Service service = new Service();
			service.setName(((QName) msgCtx.get(MessageContext.WSDL_SERVICE)).getLocalPart());
			service.setVersion(020100);
			transaction.setService(service);
			
			Operation operation = new Operation();
			operation.setMode(FlowMode.INPUT_OUTPUT);
			operation.setName(((QName) msgCtx.get(MessageContext.WSDL_OPERATION)).getLocalPart());
			transaction.setOperation(operation);
			
			HttpServletRequest servletRequest = (HttpServletRequest) msgCtx.get(MessageContext.SERVLET_REQUEST);
			Client client = new Client();
			client.setInvocationEndpoint(servletRequest.getRequestURI());
			client.setInterfaceName(((QName) msgCtx.get(MessageContext.WSDL_INTERFACE)).getLocalPart());
			if(((HttpServletRequest) msgCtx.get(MessageContext.SERVLET_REQUEST)).getUserPrincipal() != null)
				client.setPrincipal(((HttpServletRequest) msgCtx.get(MessageContext.SERVLET_REQUEST)).getUserPrincipal().getName());
			transaction.setClient(client);
		} catch (UtilsException e) {
			throw new ServiceException(e);
		}
	}
	
	public GpContext() throws ServiceException {
		loggers = new ArrayList<ILogger>();
		contexts = new ArrayList<Context>();
		openTransaction();
	}
	
	public GpContext(String correlationId) throws ServiceException {
		this();
		setCorrelationId(correlationId);
	}
	
	public String openTransaction() throws ServiceException {
		try {
			ILogger logger = LoggerFactory.newLogger(new Context());	
			loggers.add(logger);
			
			Context context = (Context) logger.getContext();
			contexts.add(context);
			
			Request request = context.getRequest();
			request.setInDate(new Date());
			
			Transaction transaction = context.getTransaction();
			transaction.setRole(Role.CLIENT);
			
			return context.getIdTransaction();
		} catch (UtilsException e) {
			throw new ServiceException(e);
		}
	}
	
	public void closeTransaction(String idTransaction) {
		if(idTransaction == null) return;
		
		Context c = getContext(idTransaction);
		if(c != null) c.setActive(false);
	}
	
	public Context getContext(){
		for(int i=contexts.size() -1; i>=0; i--) {
			if(contexts.get(i).isActive) return contexts.get(i);
		}
		return null;
	}
	
	private Context getContext(String idTransaction){
		for(Context c : contexts) {
			if(c.getIdTransaction().equals(idTransaction))
					return c;
		}
		return null;
	}
	
	public void setupNodoClient(Intermediario intermediario, Azione azione) {
		Actor to = new Actor();
		to.setName("PagoPa");
		GpThreadLocal.get().getTransaction().setTo(to);
		
		Actor from = new Actor();
		from.setName(intermediario.getDenominazione());
		GpThreadLocal.get().getTransaction().setFrom(from);
		
		GpThreadLocal.get().setInfoFruizione(PagamentiTelematiciRPTservice.SERVICE.getLocalPart(), azione.toString(), Rpt.VERSIONE_ENCODED);
		
		Server server = new Server();
		server.setName("PagoPa");
		GpThreadLocal.get().getTransaction().setServer(server);
	}
	
	private ILogger getActiveLogger(){
		for(int i=contexts.size()-1; i>=0; i--) {
			if(contexts.get(i).isActive) return loggers.get(i);
		}
		return null;
	}
	
	public void setInfoFruizione(String servizio, String operazione, int version) {
		Service service = new Service();
		service.setName(servizio);
		service.setVersion(version);
		getContext().getTransaction().setService(service);
		
		Operation operation = new Operation();
		operation.setMode(FlowMode.INPUT_OUTPUT);
		operation.setName(operazione);
		getContext().getTransaction().setOperation(operation);
	}
	
	public Transaction getTransaction() {
		return getContext().getTransaction();
	}
	
	public String getTransactionId() {
		return getContext().getIdTransaction();
	}
	
	public void setCorrelationId(String id) {
		contexts.get(0).getRequest().setCorrelationIdentifier(id);
	}

	public void setResult(GpResponse response) {
		switch (response.getCodEsitoOperazione()) {
		case OK:
			getContext().getTransaction().setResult(Result.SUCCESS);
			break;
		case INTERNAL:
			getContext().getTransaction().setResult(Result.INTERNAL_ERROR);
			break;
		default:
			getContext().getTransaction().setResult(Result.PROCESSING_ERROR);
			break;
		}
	}
	
	public void log(String string, String...params) {
		try {
			getActiveLogger().log(string, params);
		} catch (Exception e) {
			LogManager.getLogger().error("Errore nell'emissione del diagnostico", e);
		}
	}
	
	public void log() {
		for(ILogger l : loggers) {
			try {
				l.log();
			} catch (UtilsException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void log(Message m) {
		try {
			getActiveLogger().log(m);
		} catch (Exception e) {
			LogManager.getLogger().error("Errore nell'emissione della transazione", e);
		}
	}
	
	public class Context extends ProxyContext {
		private static final long serialVersionUID = 1L;
		
		private boolean isActive = true;
		
		public boolean isActive() {
			return isActive;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}
}
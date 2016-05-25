# GovPay - Porta di accesso al Nodo dei Pagamenti

GovPay implementa il protocollo di colloquio con il Nodo dei Pagamenti SPC del progetto PagoPa per l'integrazione degli enti pubblici con la rete interbancaria.

## Documentazione

* [Introduzione a GovPay](./resources/doc/pdf/GovPay-PagoPA.pdf)
* [Manuale Integrazione](./resources/doc/pdf/GovPay-ManualeIntegrazione.pdf)
* [Manuale Utente](./resources/doc/pdf/GovPay-ManualeUtente.pdf)

## Contatti

- Mailing list: [Utenti GovPay](http://www.gov4j.it/mailman/listinfo/utenti-govpay)
- [GitHub Issues](https://github.com/link-it/GovPay/issues)

## Funzionalità

Di seguito un elenco delle principali funzionalità del prodotto.
* implementazione delle Specifiche PagoPA, con supporto per pagamenti di tipo immediato, differito e ad iniziativa PSP sia monobeneficiario che multibeneficiario.
* supporto allo storno dei pagamenti.
* supporto ai pagamenti su circuito MyBank.
* supporto al pagamento della Marca da Bollo Telematica.
* aggiornato alla versione 1.7.2 delle interfacce Nodo dei Pagamenti.
* integrazione con gli applicativi dell'Ente preposti alla gestione delle posizioni creditorie tramite Web API.
* integrazione semplificata del/i portali cittadini dell'Ente.
* implementazione di servizi accessori ai pagamenti.
  * servizio di generazione IUV
  * produzione codici per BarCode e QrCode
  * produzione tracciati di Iban Accredito e Tabella Controparti
  * gestione flussi rendicontazione
  * gestione rendicontazione senza rpt
  * risoluzione pagamenti incompleti
  * giornale degli eventi
  * ...
* completa integrazione con il software di Porta di Dominio OpenSPCoop.
* cruscotto Web di gestione e configurazione.



package client;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "EUServerService", targetNamespace = "http://server/", wsdlLocation = "http://localhost:8081/EUServer/EU?wsdl")
public class EUServerService
    extends Service
{

    private final static URL EUSERVERSERVICE_WSDL_LOCATION;
    private final static WebServiceException EUSERVERSERVICE_EXCEPTION;
    private final static QName EUSERVERSERVICE_QNAME = new QName("http://server/", "EUServerService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8081/EUServer/EU?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        EUSERVERSERVICE_WSDL_LOCATION = url;
        EUSERVERSERVICE_EXCEPTION = e;
    }

    public EUServerService() {
        super(__getWsdlLocation(), EUSERVERSERVICE_QNAME);
    }

    public EUServerService(WebServiceFeature... features) {
        super(__getWsdlLocation(), EUSERVERSERVICE_QNAME, features);
    }

    public EUServerService(URL wsdlLocation) {
        super(wsdlLocation, EUSERVERSERVICE_QNAME);
    }

    public EUServerService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, EUSERVERSERVICE_QNAME, features);
    }

    public EUServerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EUServerService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns GameServer
     */
    @WebEndpoint(name = "EUServerPort")
    public GameServer getEUServerPort() {
        return super.getPort(new QName("http://server/", "EUServerPort"), GameServer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns GameServer
     */
    @WebEndpoint(name = "EUServerPort")
    public GameServer getEUServerPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://server/", "EUServerPort"), GameServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (EUSERVERSERVICE_EXCEPTION!= null) {
            throw EUSERVERSERVICE_EXCEPTION;
        }
        return EUSERVERSERVICE_WSDL_LOCATION;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;

/**
 *
 * @author Hiroshi
 */
public class LoadBalancerListenerImpl extends Listener implements LoadBalancerListener{

    private LoadBalancer lb;
    
    LoadBalancerListenerImpl(){
        
    }
    
    LoadBalancerListenerImpl(ListenerDescription description){
        Listener lbaListener = description.getListener();
        this.setInstancePort(lbaListener.getInstancePort());
        this.setInstanceProtocol(lbaListener.getInstanceProtocol());
        this.setServicePort(lbaListener.getLoadBalancerPort());
        this.setServiceProtocol(lbaListener.getProtocol());
    }
    
    public static LoadBalancerListener create(){
        return new LoadBalancerListenerImpl();
    }

    @Override
    public void setInstancePort(int instancePort) {
        super.setInstancePort(instancePort);
    }

    @Override
    public void setServicePort(int servciePort) {
        super.setLoadBalancerPort(servciePort);
    }

    @Override
    public Integer getInstancePort() {
        return super.getInstancePort();
    }

    @Override
    public Integer getServicePort() {
        return super.getLoadBalancerPort();
    }

    @Override
    public void setServiceProtocol(String serviceProtocol) {
        super.setProtocol(serviceProtocol);
    }

    @Override
    public String getInstanceProtocol() {
        return super.getInstanceProtocol();
    }

    @Override
    public String getServiceProtocol() {
        return super.getProtocol();
    }
    
    @Override
    public void setServerCertificate(String serverCertificateId){
        super.setSSLCertificateId(serverCertificateId);
    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return lb;
    }

    @Override
    public void delete() {
        if(lb != null )
            lb.deleteListener(this);
    }
    
}

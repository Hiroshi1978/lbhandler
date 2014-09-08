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
    
   /*
    * This consutructor is called from LoadBalancerImpl.class so this is defined as package private.
    */
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
        //can set only before this listener is added to any load balancer.
        if(lb == null)
            super.setInstancePort(instancePort);
        else
            throw new UnsupportedOperationException("Can not modify load balancer listener while it is attached to load balancer.");
    }

    @Override
    public void setServicePort(int servciePort) {
        //can set only before this listener is added to any load balancer.
        if(lb == null)
            super.setLoadBalancerPort(servciePort);
        else
            throw new UnsupportedOperationException("Can not modify load balancer listener while it is attached to load balancer.");
    }

    @Override
    public Integer getInstancePort() {
        
        Integer port = super.getInstancePort();
        return port == null ? -1 : port;
    }

    @Override
    public Integer getServicePort() {

        Integer port = super.getLoadBalancerPort();
        return port == null ? -1 : port;
    }

    @Override
    public void setInstanceProtocol(String instanceProtocol) {
        //can set only before this listener is added to any load balancer.
        if(lb == null)
            super.setInstanceProtocol(instanceProtocol);
        else
            throw new UnsupportedOperationException("Can not modify load balancer listener while it is attached to load balancer.");
    }

    @Override
    public void setServiceProtocol(String serviceProtocol) {
        //can set only before this listener is added to any load balancer.
        if(lb == null)
            super.setProtocol(serviceProtocol);
        else
            throw new UnsupportedOperationException("Can not modify load balancer listener while it is attached to load balancer.");
    }

    @Override
    public String getInstanceProtocol() {
        
        String protocol = super.getInstanceProtocol();
        return protocol == null ? "" : protocol;
    }

    @Override
    public String getServiceProtocol() {
        
        String protocol = super.getProtocol();
        return protocol == null ? "" : protocol;
    }
    
    @Override
    public void setServerCertificate(String serverCertificateId){
        //can set only before this listener is added to any load balancer.
        if(lb == null)
            super.setSSLCertificateId(serverCertificateId);
        else
            throw new UnsupportedOperationException("Can not modify load balancer listener while it is attached to load balancer.");
    }

    @Override
    public String getServerCertificate(){
        String certificateId = super.getSSLCertificateId();
        return certificateId == null ? "" : certificateId;
    }
    
    @Override
    public LoadBalancer getLoadBalancer() {
        return lb;
    }

   /*
    * This method should be called only from the classes in this package, for example, by LoadBalancerImplClass
    * when its instance is constructed and its listeners member is initialized.
    * Should not be called by outer codes, so this is defined as package private, not as public.
    */
    void setLoadBalancer(LoadBalancer newLb) {
        //if this load balancer listener is already attached to some load balancer,then it can only be set to null.
        if(lb != null && newLb != null)
            throw new IllegalArgumentException("Aleady attached to another load balancer.");
        lb = newLb;
    }

    @Override
    public void addTo(LoadBalancer addedTo){
        if(lb == null){
            if(!(addedTo instanceof LoadBalancerImpl))
                throw new IllegalArgumentException("Invalid load balancer specified.");
            //private field lb will be set to 'addedTo' in LoadBalancerIml#createListener()method.
            addedTo.createListener(this);
        }else if(!lb.equals(addedTo)){
            throw new IllegalArgumentException("Already attached to another load balancer.");
        }
    }
    
    @Override
    public LoadBalancer delete() {
        if(lb != null )
            //private field lb will be set to null in LoadBalancerIml#deleteListener()method.
            lb.deleteListener(this);
        return lb;
    }
    
    @Override
    public boolean equals(Object toCompare){
        
        if(toCompare instanceof LoadBalancerListenerImpl){
            
            LoadBalancerListenerImpl asImpl = (LoadBalancerListenerImpl)toCompare;
            
            boolean isAttachedToSameLoadBalancer = 
                    getLoadBalancer() == null ? asImpl.getLoadBalancer() == null :  getLoadBalancer().equals(asImpl.getLoadBalancer());
            
            return ( isAttachedToSameLoadBalancer &&
                     getInstancePort().equals(asImpl.getInstancePort()) && 
                     getInstanceProtocol().equals(asImpl.getInstanceProtocol()) &&
                     getServicePort().equals(asImpl.getServicePort()) &&
                     getServiceProtocol().equals(asImpl.getServiceProtocol()));
        }
            
        return false;
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return ( 31 * 
                 (getLoadBalancer().hashCode() + 
                  getLoadBalancerPort().hashCode() + 
                  getInstancePort().hashCode() + 
                  getInstanceProtocol().hashCode() +
                  getServicePort().hashCode() + 
                  getServiceProtocol().hashCode())    );
    }
}

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
public class LoadBalancerListenerImpl implements LoadBalancerListener{

    private LoadBalancer lb;
    
    private final Listener elbListener = new Listener();
    
    private LoadBalancerListenerImpl(Builder builder){
        
        elbListener.setInstancePort(builder.instancePort);
        elbListener.setInstanceProtocol(builder.instanceProtocol);
        elbListener.setLoadBalancerPort(builder.servicePort);
        elbListener.setProtocol(builder.serviceProtocol);
        elbListener.serCertificateId(builder.certificateId);
    }
    
    @Override
    public void setInstancePort(int instancePort) {
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
    }

    @Override
    public void setServicePort(int servciePort) {
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
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
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
    }

    @Override
    public void setServiceProtocol(String serviceProtocol) {
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
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
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
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
    * This method should be called only from the instances of LoadBalancerImpl class in this package.
    * Should not called by this class itself.
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
            //private field lb will be set to 'addedTo' in LoadBalancerIml#createListener method.
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
                 ((getLoadBalancer() == null ? 0 : getLoadBalancer().hashCode()) + 
                  getLoadBalancerPort().hashCode() + 
                  getInstancePort().hashCode() + 
                  getInstanceProtocol().hashCode() +
                  getServicePort().hashCode() + 
                  getServiceProtocol().hashCode())    );
    }
    
    public static class Builder{
        
        private int instancePort = 80;
        private String instanceProtocol = "HTTP";
        private int servicePort = 80;
        private String serviceProtocol = "HTTP";
        private String certificateId;
        
        public Builder instancePort(int instancePort){
            this.instancePort = instancePort;
            return this;
        }
        public Builder instanceProtocol(int instanceProtocol){
            this.instanceProtocol = instanceProtocol;
            return this;
        }
        public Builder servicePort(int servicePort){
            this.servicePort = servicePort;
            return this;
        }
        public Builder serviceProtocol(String serviceProtocol){
            this.serviceProtocol = serviceProtocol;
            return this;
        }
        public Builder certificateId(String certificateId){
            this.certificateId = certificateId;
            return this;
        }
        public Builder description(LoadBalancerListenerDescription desc){

            Listener source = description.getListener();
            this.instancePort = source.getInstancePort();
            this.instanceProtocol = source.getInstanceProtocol();
            this.servicePort = source.getLoadBalancerPort();
            this.serviceProtocol = source.getProtocol();
            this.certificateId = source.getSSLCertificateId();
        }
        
        public LoadBalancerListener build(){
            return new LoadBalancerListenerImpl(this);
        }
    }
}

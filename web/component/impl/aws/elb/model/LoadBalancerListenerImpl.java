/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.elb.model;

import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.ListenerDescription;
import web.component.api.model.LoadBalancer;
import web.component.api.model.LoadBalancerListener;

/**
 *
 * @author Hiroshi
 */
public class LoadBalancerListenerImpl implements LoadBalancerListener{

    private final Listener elbListener = new Listener();
    
    private LoadBalancerListenerImpl(Builder builder){
        
        elbListener.setInstancePort(builder.instancePort);
        elbListener.setInstanceProtocol(builder.instanceProtocol);
        elbListener.setLoadBalancerPort(builder.servicePort);
        elbListener.setProtocol(builder.serviceProtocol);
        elbListener.setSSLCertificateId(builder.certificateId);
    }
    
    Listener asElbListener(){
        return elbListener;
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
        return elbListener.getInstancePort();
    }

    @Override
    public Integer getServicePort() {
        return elbListener.getLoadBalancerPort();
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
        return elbListener.getInstanceProtocol();
    }

    @Override
    public String getServiceProtocol() {
        return elbListener.getProtocol();
    }
    
    @Override
    public void setServerCertificate(String serverCertificateId){
        throw new UnsupportedOperationException("Can not modify load balancer listener.");
    }

    @Override
    public String getServerCertificate(){
        String certificateId = elbListener.getSSLCertificateId();
        return certificateId == null ? "" : certificateId;
    }
    
    @Override
    public LoadBalancer getLoadBalancer() {
        throw new UnsupportedOperationException("Not yet supported.");
    }

   /*
    * This method should be called only from the instances of LoadBalancerImpl class in this package.
    * Should not called by this class itself.
    */
    void setLoadBalancer(LoadBalancer newLb) {
        throw new UnsupportedOperationException("Not yet supported.");
    }

    @Override
    public void addTo(LoadBalancer addedTo){
        addedTo.createListener(this);
    }
    
    @Override
    public void deleteFrom(LoadBalancer deletedFrom) {
        deletedFrom.deleteListener(this);
    }
    
    @Override
    public boolean equals(Object toCompare){
        
        if(toCompare instanceof LoadBalancerListenerImpl){
            
            LoadBalancerListenerImpl asImpl = (LoadBalancerListenerImpl)toCompare;
            
            return ( getInstancePort().equals(asImpl.getInstancePort()) && 
                     getInstanceProtocol().equalsIgnoreCase(asImpl.getInstanceProtocol()) &&
                     getServicePort().equals(asImpl.getServicePort()) &&
                     getServiceProtocol().equalsIgnoreCase(asImpl.getServiceProtocol()));
        }
            
        return false;
    }
    
    @Override
    public int hashCode(){
        //this is wrong, but don't know how to implement this method properly.
        return ( 31 * 
                 ( getInstancePort().hashCode() + 
                   getInstanceProtocol().toLowerCase().hashCode() +
                   getServicePort().hashCode() + 
                   getServiceProtocol().toLowerCase().hashCode() +
                   ( getServerCertificate() == null ? 0 : getServerCertificate().hashCode() ) ) );
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
        public Builder instanceProtocol(String instanceProtocol){
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
        public Builder description(ListenerDescription desc){

            Listener source = desc.getListener();
            this.instancePort = source.getInstancePort();
            this.instanceProtocol = source.getInstanceProtocol();
            this.servicePort = source.getLoadBalancerPort();
            this.serviceProtocol = source.getProtocol();
            this.certificateId = source.getSSLCertificateId();
            return this;
        }
        
        public LoadBalancerListener build(){
            return new LoadBalancerListenerImpl(this);
        }
    }
}

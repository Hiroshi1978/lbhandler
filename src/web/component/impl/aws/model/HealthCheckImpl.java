/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws.model;

import web.component.api.model.HealthCheck;

/**
 *
 * @author Hiroshi
 */
public class HealthCheckImpl extends AWSModelBase implements HealthCheck{

    private final com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck;
    
    private HealthCheckImpl(com.amazonaws.services.elasticloadbalancing.model.HealthCheck source){
        
        elbHealthCheck = copyElbHealthCheck(source);
    }
    
    private static HealthCheck create(Builder builder){
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck elbHealthCheck = new com.amazonaws.services.elasticloadbalancing.model.HealthCheck();
        elbHealthCheck.setHealthyThreshold(builder.healthThreshold);
        elbHealthCheck.setInterval(builder.interval);
        elbHealthCheck.setTarget(builder.target);
        elbHealthCheck.setTimeout(builder.timeout);
        elbHealthCheck.setUnhealthyThreshold(builder.unhealthThreshold);
        return new HealthCheckImpl(elbHealthCheck);
    }
    
    private com.amazonaws.services.elasticloadbalancing.model.HealthCheck copyElbHealthCheck(com.amazonaws.services.elasticloadbalancing.model.HealthCheck source){
        
        com.amazonaws.services.elasticloadbalancing.model.HealthCheck copy = new com.amazonaws.services.elasticloadbalancing.model.HealthCheck();
        copy.setHealthyThreshold(source.getHealthyThreshold());
        copy.setInterval(source.getInterval());
        copy.setTarget(source.getTarget());
        copy.setTimeout(source.getTimeout());
        copy.setUnhealthyThreshold(source.getUnhealthyThreshold());
        
        return copy;
    }
    
    com.amazonaws.services.elasticloadbalancing.model.HealthCheck asElbHealthCheck(){
        return copyElbHealthCheck(elbHealthCheck);
    }
    
    @Override
    public int getHealthyThreshold() {
        return elbHealthCheck.getHealthyThreshold();
    }

    @Override
    public int getInterval() {
        return elbHealthCheck.getInterval();
    }

    @Override
    public String getTarget() {
        return elbHealthCheck.getTarget();
    }

    @Override
    public int getTimeout() {
        return elbHealthCheck.getTimeout();
    }

    @Override
    public int getUnhealthyThreshold() {
        return elbHealthCheck.getUnhealthyThreshold();
    }

    @Override
    public void setHealthyThreshold(int healthyThreshold) {
        elbHealthCheck.setHealthyThreshold(healthyThreshold);
    }

    @Override
    public void setInterval(int interval) {
        elbHealthCheck.setInterval(interval);
    }

    @Override
    public void setTarget(String target) {
        elbHealthCheck.setTarget(target);
    }

    @Override
    public void setTimeout(int timeout) {
        elbHealthCheck.setTimeout(timeout);
    }

    @Override
    public void setUnhealthyThreshold(int unhealthyThreshold) {
        elbHealthCheck.setUnhealthyThreshold(unhealthyThreshold);
    }
    
    @Override
    public String toString(){
        return elbHealthCheck.toString();
    }
    
    @Override
    public boolean equals(Object o){
       
        if(o instanceof HealthCheckImpl){
            HealthCheckImpl asImpl = (HealthCheckImpl)o;
            if( getHealthyThreshold() == asImpl.getHealthyThreshold() &&
                getInterval() == asImpl.getInterval() &&
                getTarget().equals(asImpl.getTarget()) &&
                getTimeout() == asImpl.getTimeout() &&
                getUnhealthyThreshold() == asImpl.getUnhealthyThreshold() )
                return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return 31 * elbHealthCheck.hashCode();
    }
    
    static class Builder {
        
        private Integer healthThreshold;
        private Integer interval;
        private String target;
        private Integer timeout;
        private Integer unhealthThreshold;
        
        public Builder healthyThreshold(int ht){
            this.healthThreshold = ht;
            return this;
        }
        public Builder interval(int iv){
            this.interval = iv;
            return this;
        }
        public Builder target(String target){
            this.target = target;
            return this;
        }
        public Builder timeout(int to){
            this.timeout = to;
            return this;
        }
        public Builder unhealthyThreshold(int uht){
            this.unhealthThreshold = uht;
            return this;
        }

        public HealthCheck build(){
            
            return HealthCheckImpl.create(this);
        }
    }
}

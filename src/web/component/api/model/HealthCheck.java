/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.model;

/**
 *
 * @author Hiroshi
 */
public interface HealthCheck {
    
    public int getHealthyThreshold();
    public int getInterval();
    public String getTarget();
    public int getTimeout();
    public int getUnhealthyThreshold();
    
    public void setHealthyThreshold(int healthyThreshold);
    public void setInterval(int interval);
    public void setTarget(String target);
    public void setTimeout(int timeout);
    public void setUnhealthyThreshold(int unhealthyThreshold);

}

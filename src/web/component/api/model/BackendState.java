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
public interface BackendState {
    
    //interface as elb instance.

    public String getInstanceId();
    public String getDescription();
    public String getReasonCode();
    public String getState();
}

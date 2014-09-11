/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.api.aws.elb.model;

/**
 *
 * @author Hiroshi
 */
public interface BackendInstanceState {
    public String getDescription();
    public String getId();
    public String getReasonCode();
    public String getState();
}
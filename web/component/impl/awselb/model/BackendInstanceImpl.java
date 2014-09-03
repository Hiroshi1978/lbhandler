/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.awselb.model;

import com.amazonaws.services.elasticloadbalancing.model.Instance;
import web.component.api.model.BackendInstance;
import web.component.api.model.LoadBalancer;

/**
 *
 * @author Hiroshi
 */
public class BackendInstanceImpl extends Instance implements BackendInstance{

    private LoadBalancer lb;
    private final String id;
    
    private BackendInstanceImpl(String id){
        this.id = id;
    }
    
    @Override
    public LoadBalancer getLoadBalancer() {
        return lb;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void register(LoadBalancer newLb) {
        if(lb == null || !lb.equals(newLb)){
            newLb.registerInstance(this);
            lb = newLb;
        }
    }

    @Override
    public void deregister() {
        if(lb != null)
            lb.deregisterInstance(this);
    }

}

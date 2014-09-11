/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import java.util.HashMap;
import java.util.Map;
import web.component.impl.Cloud;
import web.component.impl.CloudComponent;
import web.component.impl.aws.ec2.AWSEC2;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class AWS implements Cloud{
    
    private static AWS instance = new AWS();
    
    private final Map<ComponentName, CloudComponent> components = new HashMap<>();

    private AWS(){
        components.put(ComponentName.EC2, AWSEC2.create());
        components.put(ComponentName.ELB, AWSELB.create());        
    }
    
    public static CloudComponent get(ComponentName name){
        return instance.components.get(name);
    }
    
    public static enum ComponentName{
        EC2,ELB;
    }
}

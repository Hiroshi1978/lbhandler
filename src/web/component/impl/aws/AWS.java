/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import java.util.HashMap;
import java.util.Map;
import web.component.impl.Cloud;
import web.component.impl.CloudBlock;
import web.component.impl.aws.ec2.AWSEC2;
import web.component.impl.aws.elb.AWSELB;

/**
 *
 * @author Hiroshi
 */
public class AWS implements Cloud{
    
    private static final AWS INSTANCE = new AWS();
    
    private final Map<BlockName, CloudBlock> components = new HashMap<>();

    private AWS(){     
    }
    
    public static final CloudBlock get(BlockName name){
        
        CloudBlock cb = INSTANCE.components.get(name);
        
        if(cb == null){
        
            if(BlockName.EC2.equals(name))
                cb = AWSEC2.get();
            if(BlockName.ELB.equals(name))
                cb = AWSELB.get();
            
            INSTANCE.components.put(name, cb);
        }
        return cb;
    }
    
    public static enum BlockName{
        EC2,ELB;
    }
}

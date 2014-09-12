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
    
    private static AWS instance = new AWS();
    
    private final Map<BlockName, CloudBlock> components = new HashMap<>();

    private AWS(){
        components.put(BlockName.EC2, AWSEC2.get());
        components.put(BlockName.ELB, AWSELB.get());        
    }
    
    public static CloudBlock get(BlockName name){
        return instance.components.get(name);
    }
    
    public static enum BlockName{
        EC2,ELB;
    }
}

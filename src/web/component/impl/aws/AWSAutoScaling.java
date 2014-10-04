/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import java.util.Properties;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWSAutoScaling implements CloudBlock{
    
    private final AmazonAutoScaling awsHttpClient;
    
    private AWSAutoScaling(){
        
        AmazonAutoScalingClient initialClient = new AmazonAutoScalingClient(AWS.access().credentials());
        setUpHttpClient(initialClient);
        awsHttpClient = initialClient;
    }
    
    private void setUpHttpClient(AmazonAutoScalingClient awsAutoScalingClient){

        Properties conf = AWS.access().conf();
        String endpoint = conf.getProperty("autoscaling.endpoint");
        String serviceName = conf.getProperty("autoscaling.servicename");
        String regionName = conf.getProperty("region");
        
        awsAutoScalingClient.setEndpoint(endpoint, serviceName, regionName);
        awsAutoScalingClient.setServiceNameIntern(serviceName);
    }
    
    static AWSAutoScaling get(){
        return new AWSAutoScaling();
    }
}

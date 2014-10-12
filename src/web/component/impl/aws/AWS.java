/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package web.component.impl.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import web.component.impl.Cloud;
import web.component.impl.CloudBlock;

/**
 *
 * @author Hiroshi
 */
public class AWS implements Cloud{
    

    private static final String CONF_DIR_PROP_KEY = "aws.config.file.dir";
    private static final String CONF_FILE_NAME = "aws_config.txt";
    
    private static final AWS INSTANCE = new AWS();

    private final Properties CONF = new Properties();    
    private final AWSCredentials AWS_CREDENTIALS;
    private final Map<BlockName, CloudBlock> components = new HashMap<>();

    private AWS(){
        
        loadClientConfig();
        AWS_CREDENTIALS = new BasicAWSCredentials(CONF.getProperty("aws.key"),CONF.getProperty("aws.secret"));  
    }
    
    private void loadClientConfig(){
        
        try {
            
            System.out.println(System.getProperty("user.dir"));
            StringBuilder confPath = 
                    new StringBuilder(System.getProperty(CONF_DIR_PROP_KEY, System.getProperty("user.dir")));
            if(!confPath.toString().endsWith(File.separator))
                confPath.append(File.separator);
            String confFile = confPath.append(CONF_FILE_NAME).toString();
            System.out.println("Loading aws client configuration from [" + confFile + "] ...");
            CONF.load(new FileInputStream(confFile));
            
        } catch (IOException | RuntimeException ex) {
            System.out.println(
                    "failed to load aws client configuration. \n"
                    + "You must specify '" + CONF_DIR_PROP_KEY + "' system property to the path of the directory where your aws client configuration file exists.\n"
                    + "Otherwise you should place your aws client configuration file to the directory 'user.dir' system property indicates."
            );
        }
    }
        
    //interface to access cloud.
    public static AWS access(){
        return INSTANCE;
    }
    public AWSAutoScaling as(){
        return (AWSAutoScaling)get(BlockName.AutoScaling);
    }
    public AWSEC2 ec2(){
        return (AWSEC2)get(BlockName.EC2);
    }
    public AWSELB elb(){
        return (AWSELB)get(BlockName.ELB);
    }
    
    
    private final CloudBlock get(BlockName name){
        
        CloudBlock cb = components.get(name);
        
        if(cb == null){
        
            if(BlockName.EC2.equals(name))
                cb = AWSEC2.get();
            if(BlockName.ELB.equals(name))
                cb = AWSELB.get();
            if(BlockName.AutoScaling.equals(name))
                cb = AWSAutoScaling.get();
            
            components.put(name, cb);
        }
        return cb;
    }
    
   /*
    * returns a copy instancd of AWS client config.
    */
    Properties conf(){

        Properties copyConf = new Properties();
        
        List<Object> keys = new ArrayList<>(CONF.keySet());
        for(Object key : keys){
            String keyString = (String)key;
            copyConf.setProperty(keyString, CONF.getProperty(keyString));
        }
        
        return copyConf;
    }
    
   /*
    * returns a copy instancd of AWS client credentials.
    */
    AWSCredentials credentials(){
        
        String awsKey = AWS_CREDENTIALS.getAWSAccessKeyId();
        String secretKey = AWS_CREDENTIALS.getAWSSecretKey();
        
        //return a copy instance.
        return new BasicAWSCredentials(awsKey, secretKey);
    }
    
    private static enum BlockName{
        EC2,ELB,AutoScaling;
    }
}

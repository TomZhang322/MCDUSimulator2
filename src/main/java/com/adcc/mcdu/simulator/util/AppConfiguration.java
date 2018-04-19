package com.adcc.mcdu.simulator.util;

import org.yaml.snakeyaml.Yaml;

import com.adcc.mcdu.simulator.ibmmq.IBMMQFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AppConfiguration {
    private static AppConfiguration appConfiguration = null;

    private static String path = System.getProperty("user.dir")+ File.separator+"conf"+ File.separator+"conf.yml";

    private IBMMQFactory ibmmqFactory;    
    
    private String masRuquestFlag;

    private AppConfiguration() {}

    private static AppConfiguration getAppConfiguration()
    {
        AppConfiguration appConfiguration1 = null;
        try {
            File f = new File(path);
            Yaml yaml = new Yaml();
            FileInputStream fi = new FileInputStream(f.getAbsolutePath());
            appConfiguration1 = yaml.loadAs(fi, AppConfiguration.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return appConfiguration1;
    }

    public synchronized static AppConfiguration getInstance()
    {
        if(appConfiguration == null)
        {
            appConfiguration = getAppConfiguration();
            return  appConfiguration;
        }
        return appConfiguration;
    }

    public IBMMQFactory getIbmmqFactory() {
        return ibmmqFactory;
    }

    public void setIbmmqFactory(IBMMQFactory ibmmqFactory) {
        this.ibmmqFactory = ibmmqFactory;
    }

	public String getMasRuquestFlag() {
		return masRuquestFlag;
	}

	public void setMasRuquestFlag(String masRuquestFlag) {
		this.masRuquestFlag = masRuquestFlag;
	}		

}

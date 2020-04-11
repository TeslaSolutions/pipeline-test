package com.dream.configs;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfileManager {

	@Autowired
	private Environment env;
	
    /**
     * {@link Environment#getActiveProfiles} returns an array of profiles. 
     * On of those profiles is either qa or production.
     * We are not interested in other profiles that may go along with qa - production just that main profile 
     */
    public String getActiveProfile(){
    	String profile = "";
    	 for (final String profileName : env.getActiveProfiles()) {
             if(profileName.equalsIgnoreCase("qa") || profileName.equalsIgnoreCase("prod") || profileName.equalsIgnoreCase("test")){
            	 profile= profileName;
             }
         }
    	 return profile;
    }

    public boolean isProduction(){
        return Lists.newArrayList(env.getActiveProfiles()).stream().anyMatch(p->p.equals("prod"));
    }

    public boolean isTest(){
        return Lists.newArrayList(env.getActiveProfiles()).stream().anyMatch(p->p.equalsIgnoreCase("integration") || p.equalsIgnoreCase("test"));
    }
}
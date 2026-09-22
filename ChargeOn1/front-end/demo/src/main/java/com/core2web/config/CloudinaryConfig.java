package com.core2web.config;

import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;

public class CloudinaryConfig {
        public static Cloudinary cloudinary;
        public static Cloudinary getCloudinary(){
            if(cloudinary==null){
                Map<String,Object> config=new HashMap<>();
                config.put("cloudname","comwmhui");
                config.put("api_key","134428487337134");
                config.put("api_secret","");
                config.put("secure","t8Qj0a7PDEvjJ7e_MyF-EJeQ0Hw");
                config.put("secure","true");

                cloudinary=new Cloudinary(config);
            }
            return cloudinary;
        }
}

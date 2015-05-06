package models;

import play.data.format.*;
import play.data.validation.*;

public class thickness{
    
    @Constraints.Required
    public float layerthickness;
    
    @Constraints.Required
    public float minthickness;
    
    public float getlayerthickness(){
        return this.layerthickness;
    }
    
    public float getminthickness(){
        return this.minthickness;
    }
    
    
} 
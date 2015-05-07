package controllers;

import play.*;
import play.mvc.*;
import java.io.File;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Results;
import play.data.Form;
import converter.converter;
import java.util.*;

//import models
import models.objFile;
import models.stlFile;
import models.thickness;

//import transactional
import play.db.ebean.Transactional;

import views.html.*;

public class Application extends Controller {
    
    
    final static Form<thickness> thicknessForm = Form.form(thickness.class);
    

    public static Result index() {
        return redirect(controllers.routes.Application.home());
    }
    
    public static Result home(){
        
        String testStr = "hello world";
        
        return ok(home.render("hello world"));
        
    }
    
    
    @Transactional
    public static Result upload() {
        
        //get file
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart picture = body.getFile("objfile");
        
        
        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType(); 
            File file = picture.getFile();
            
            
            //generating uuid for file uploaded
            Long uuid = UUID.randomUUID().getMostSignificantBits();
            //rename the file to new file name
            fileName = String.valueOf(uuid) + "_" + fileName.trim();
            
            
            // handle uploaded obj file
            String myUploadPath = Play.application().configuration().getString("objUploadPath");
            file.renameTo(new File(myUploadPath, fileName));
            
            
            //create a record of obj file
            objFile newObjFile = new objFile();
            newObjFile.pathName = myUploadPath+fileName;
            newObjFile.fileName = fileName;
            //save the uploaded file
            newObjFile.save();
            
            //console info
            System.err.println(newObjFile.id);
            System.err.println(newObjFile.pathName);
            System.out.println("Uploaded file: "+file.getAbsolutePath());
            
            return redirect(controllers.routes.Application.showObj(newObjFile.id));
            
            // return ok("File uploaded");
        } else {
            flash("error", "Missing file");
            return redirect(routes.Application.index());    
        }
    }
    
    
    public static Result showObj(Long id){
        
        try{
            objFile obj = objFile.find.byId(id);
            
            String filename=obj.fileName;
            
            return ok(showObj.render(filename,obj.id,thicknessForm));
            
            // return ok("Get your file");
        }
        catch(NullPointerException e) {
            return notFound("<h1>Page not found</h1>").as("text/html");
        }
    }
    
    @Transactional
    public static Result convertObjFile(Long id){
        try{
            objFile obj = objFile.find.byId(id);
            
            float layerthickness;
            float minthickness;
            
            //get thickness info
            Form<thickness> filledThicknessForm = thicknessForm.bindFromRequest();
            
            //if form error, assign default value
            if(filledThicknessForm.hasErrors()){
                layerthickness = 0.5f;
                minthickness = 0.1f;
            }else{
                thickness newTKsetting = filledThicknessForm.get();
                layerthickness = newTKsetting.getlayerthickness();
                minthickness = newTKsetting.getminthickness();
            }
            
                
            //console info
            System.out.println("layer thickness is set to be "+layerthickness);
            System.out.println("minimum thickness is set to be "+minthickness);
            
            //console info
            System.err.println(obj.pathName);
            System.err.println(obj.fileName);
            // File thefile = new File(obj.pathName);
            
            //generating uuid for resulted stl
            Long uuid = UUID.randomUUID().getMostSignificantBits();
            //rename the file to new file name
            String stlFileName = String.valueOf(uuid) + "_hollow.stl";
            String stlFileName_inner = String.valueOf(uuid) + "_innerhollow.stl";
            
            converter ObjConverter = new converter();
            String objFilePath = obj.pathName;
            String stlFilePath = Play.application().configuration().getString("stlServePath")+stlFileName;
            String innerStlFilePath = Play.application().configuration().getString("stlServePath") + stlFileName_inner;
            
            //set layer thickness
            ObjConverter.setThickness(layerthickness);
            ObjConverter.setMinThicknessUnit(minthickness);
            
            //start conversion procedure
            ObjConverter.convertObj(objFilePath,stlFilePath,innerStlFilePath);
            
            //create stl file record in the database
            stlFile newStl = new stlFile();
            newStl.objfile = obj;
            newStl.fileName = stlFileName;
            newStl.pathName = stlFilePath;
            newStl.save();
            
            //create inner stl file record in the database
            stlFile newInnerStlFile = new stlFile();
            newInnerStlFile.objfile = obj;
            newInnerStlFile.fileName = stlFileName_inner;
            newInnerStlFile.pathName = innerStlFilePath;
            newInnerStlFile.save();
            
            return redirect(controllers.routes.Application.downloadStls(newStl.id));
            
            // return ok("Get your file");
        }
        catch(NullPointerException e) {
            return notFound("<h1>Page not found</h1>").as("text/html");
        }
    }
    
    
    
    public static Result showStls(Long id){
        try{
            objFile obj = objFile.find.byId(id);
            
            List<stlFile> stlList = obj.stlFiles;
            stlFile stl = stlList.get(0);
            stlFile innerstl = stlList.get(1);
            
            // return ok(showObj.render(obj.fileName));
            
            return ok("To be implemented");
        }
        catch(NullPointerException e) {
            return notFound("<h1>Page not found</h1>").as("text/html");
        }
    }
    
    
    public static Result downloadStls(Long id){
        try{
            stlFile stl = stlFile.find.byId(id);
            objFile obj = stl.objfile;
            Long objId = obj.id;
            String stlName = stl.fileName;
            return ok(downloadStls.render(stlName, objId));
        }
        catch(NullPointerException e) {
            return notFound("<h1>Page not found</h1>").as("text/html");
        }
    }
    

}

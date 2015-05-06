package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import models.objFile;

@Entity 
public class stlFile extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;
  
  @ManyToOne
  @Constraints.Required
  public objFile objfile;
  
  @Constraints.Required
  public String pathName;
  
  @Constraints.Required
  public String fileName;
  
  
  @Formats.DateTime(pattern="dd/MM/yyyy")
  public Date addedDate = new Date();
  
  public static Finder<Long,stlFile> find = new Finder<Long,stlFile>(
    Long.class, stlFile.class
  ); 

}
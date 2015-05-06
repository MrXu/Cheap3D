package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import models.stlFile;

@Entity 
public class objFile extends Model {

  @Id
  @Constraints.Min(10)
  public Long id;
  
  @Constraints.Required
  public String fileName;
  
  @Constraints.Required
  public String pathName;
  
  
  @Formats.DateTime(pattern="dd/MM/yyyy")
  public Date dueDate = new Date();
  
  @OneToMany(mappedBy="objfile", cascade = CascadeType.ALL)
  public List<stlFile> stlFiles;
  
  public static Finder<Long,objFile> find = new Finder<Long,objFile>(
    Long.class, objFile.class
  ); 

}
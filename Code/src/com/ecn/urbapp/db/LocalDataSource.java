package com.ecn.urbapp.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalDataSource {
	
	//Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	public MySQLiteHelper getDbHelper() {
		return dbHelper;
	}


	private String[] allColumnsProject = {MySQLiteHelper.COLUMN_PROJECTID, MySQLiteHelper.COLUMN_PROJECTNAME, MySQLiteHelper.COLUMN_GPSGEOMID};
	private String[] allColumnsPhoto = {MySQLiteHelper.COLUMN_PHOTOID, MySQLiteHelper.COLUMN_PHOTODESCRIPTION, MySQLiteHelper.COLUMN_PHOTOAUTHOR, MySQLiteHelper.COLUMN_PHOTOURL, MySQLiteHelper.COLUMN_GPSGEOMID};
	private String[] allColumnsGpsGeom = {MySQLiteHelper.COLUMN_GPSGEOMID, MySQLiteHelper.COLUMN_GPSGEOMCOORD};
	private String[] allColumnsPixelGeom = {MySQLiteHelper.COLUMN_PIXELGEOMID, MySQLiteHelper.COLUMN_PIXELGEOMCOORD};
	private String[] allColumnsMaterial = {MySQLiteHelper.COLUMN_MATERIALID, MySQLiteHelper.COLUMN_MATERIALNAME};
	private String[] allColumnsElementType = {MySQLiteHelper.COLUMN_ELEMENTTYPEID, MySQLiteHelper.COLUMN_ELEMENTTYPENAME};
	private String[] allColumnsComposed = {MySQLiteHelper.COLUMN_PROJECTID, MySQLiteHelper.COLUMN_PHOTOID};
	private String[] allColumnsElement = {MySQLiteHelper.COLUMN_ELEMENTID, MySQLiteHelper.COLUMN_PHOTOID,MySQLiteHelper.COLUMN_MATERIALID, MySQLiteHelper.COLUMN_GPSGEOMID,MySQLiteHelper.COLUMN_PIXELGEOMID, MySQLiteHelper.COLUMN_ELEMENTTYPEID, MySQLiteHelper.COLUMN_ELEMENTCOLOR};
		
	//constructor
	public LocalDataSource(Context context){
		dbHelper = new MySQLiteHelper(context);
	}
		
	//Open and close database
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	/**
	 * creating a new project in the database
	 * @param str
	 * @return
	 */
	public Project createProject (String str){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
		long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = 
				database.query(
						MySQLiteHelper.TABLE_PROJECT,
						allColumnsProject,
						MySQLiteHelper.COLUMN_PROJECTID+" = "+insertId,
						null, null, null, null);
		cursor.moveToFirst();
		Project newProject = cursorToProject(cursor);//method at the end of the class
		cursor.close();
		return newProject;
	}
	

	
	//surcharge
	public Project createProject (long id, String str){
        Boolean exist = existProjectWithId(id);
        
        if(exist == true){
        	Project existProject = getProjectWithId(id);
        	Project updatedProject = updateProject(id, existProject, str);
            return updatedProject;
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_PROJECTID, id);
            values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
            long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null,
                    values);
            Cursor cursor = database.query(MySQLiteHelper.TABLE_PROJECT,
                    allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Project p2 = cursorToProject(cursor);
            cursor.close();
            return p2;
        }
    }
	
	public Project updateProject(Long id, Project project, String descr){
        ContentValues values = new ContentValues();
 
        values.put(MySQLiteHelper.COLUMN_PROJECTNAME, descr);
 
        database.update(MySQLiteHelper.TABLE_PROJECT, values, MySQLiteHelper.COLUMN_PROJECTID + " = " +project.getProjectId(), null);
 
        return getProjectWithId(project.getProjectId());
    }
	
    public Project getProjectWithId(Long id){
        Cursor c = database.query(MySQLiteHelper.TABLE_PROJECT, allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = \"" + id +"\"", null, null, null, null);
        c.moveToFirst();
        Project p1 = cursorToProject(c);
        c.close();
        return p1;
    }
    
    public Boolean existProjectWithId(Long id){
        Cursor c = database.query(MySQLiteHelper.TABLE_PROJECT, allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = \"" + id +"\"", null, null, null, null);
        if(c.getCount()>0){
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }
	
	
	
	public void deleteProject(Project p1){
		long id = p1.getProjectId();
		System.out.println("Project deleted with id: "+ id);
		database.delete(MySQLiteHelper.TABLE_PROJECT, MySQLiteHelper.COLUMN_PROJECTID+" = "+ id, null);
	}
	
	/**
	 * query to get project information
	 * 
	 */
	private static final String
		GETALLPROJECTS = 
			"SELECT * FROM "
			+ MySQLiteHelper.TABLE_PROJECT 
			+ " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM 
			+" ON "+MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_GPSGEOMID+"="+MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID
			+";"
		;
	
	
	/**
	 * query to get photo informations
	 * 
	 */
	private static final String
		GETALLPHOTOS = 
			"SELECT * FROM "
			+ MySQLiteHelper.TABLE_PHOTO 
			+ " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM 
			+" ON "+MySQLiteHelper.TABLE_PHOTO+"."+MySQLiteHelper.COLUMN_GPSGEOMID+"="+MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID
			+";"
		;
	
	/**
	 * execution of the query
	 * @return
	 */
	public List<Project> getAllProjects(){
		List<Project> projectsList = new ArrayList<Project>();
		
		Cursor cursor = database.rawQuery(GETALLPROJECTS,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Project p1 = cursorToProject(cursor);
			projectsList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return projectsList;
	}
	
	
	


  private Project cursorToProject(Cursor cursor) {
    Project p1 = new Project();
    p1.setProjectId(cursor.getLong(0));
    p1.setProjectName(cursor.getString(1));
    p1.setGpsGeom_id(cursor.getLong(2)); 
    //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
    try{
    	p1.setExt_GpsGeomCoord(cursor.getString(4));
    }
    catch (Exception e){};
    return p1;
	
  }
  
  // PHOTO METHODS
  
	public List<Photo> getAllPhotos(){
		List<Photo> photosList = new ArrayList<Photo>();
		
		Cursor cursor = database.rawQuery(GETALLPHOTOS,null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Photo p1 = cursorToPhoto(cursor);
			photosList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		return photosList;
	}
	
	public void deletePhoto(Photo p1){
		long id = p1.getPhoto_id();
		System.out.println("Photo deleted with id: "+ id);
		database.delete(MySQLiteHelper.TABLE_PHOTO, MySQLiteHelper.COLUMN_PHOTOID+" = "+ id, null);
	}
  
  public Photo createPhoto (String descr, String author, String url){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_PHOTODESCRIPTION, descr);
		values.put(MySQLiteHelper.COLUMN_PHOTOAUTHOR, author);
		values.put(MySQLiteHelper.COLUMN_PHOTOURL, url);
		long insertId = database.insert(MySQLiteHelper.TABLE_PHOTO, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = 
				database.query(
						MySQLiteHelper.TABLE_PHOTO,
						allColumnsPhoto,
						MySQLiteHelper.COLUMN_PHOTOID+" = "+insertId,
						null, null, null, null);
		cursor.moveToFirst();
		Photo newPhoto = cursorToPhoto(cursor);//method at the end of the class
		cursor.close();
		return newPhoto;
  }
  
  private Photo cursorToPhoto(Cursor cursor) {
	    Photo p1 = new Photo();
	    p1.setPhoto_id(cursor.getLong(0));
	    p1.setPhoto_description(cursor.getString(1));
	    p1.setPhoto_author(cursor.getString(2)); 
	    p1.setPhoto_url(cursor.getString(3)); 
	    p1.setGps_Geom_id(cursor.getLong(4)); 
	    //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
	    try{
	    	p1.setExt_GpsGeomCoord(cursor.getString(6));
	    }
	    catch (Exception e){};
	    return p1;
		
	  }
  
  
  
  // GPS GEOM METHODS
		
	/**
	 * create a GPSGeom in the database and update the photo tuple where photo_id = id with this gpsgeom_id
	 * @param str
	 * @param id
	 * @return
	 */
	public GpsGeom createGPSGeomToPhoto (String str, long id){
		GpsGeom gps1 = createGPSGeom(str);
		//TODO TRANSACTION
		 ContentValues args = new ContentValues();
		 args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
		 int d = database.update(MySQLiteHelper.TABLE_PHOTO, args, MySQLiteHelper.COLUMN_PHOTOID +"=" + id, null);
		 
		 return gps1;
	}

	  
		/**
		 * create a GPSGeom in the database and update the project tuple where project_id = id with this gpsgeom_id
		 * @param str
		 * @return
		 */
		public GpsGeom createGPSGeomToProject (String str, long id){
			GpsGeom gps1 = createGPSGeom(str);
			//TODO TRANSACTION
			 ContentValues args = new ContentValues();
			 args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
			 int d = database.update(MySQLiteHelper.TABLE_PROJECT, args, MySQLiteHelper.COLUMN_PROJECTID +"=" + id, null);
			
			return gps1;
		}
	
	/**
	 * create a GPSGeom with the gpsgeom_coord str
	 * @param str
	 * @return
	 */
	public GpsGeom createGPSGeom (String str){
		ContentValues values = new ContentValues(); 
		values.put(MySQLiteHelper.COLUMN_GPSGEOMCOORD, str);
		long insertId = database.insert(MySQLiteHelper.TABLE_GPSGEOM, null, values);
		//TODO check the utily of autoincrement
		Cursor cursor = 
				database.query(
						MySQLiteHelper.TABLE_GPSGEOM,
						allColumnsGpsGeom,
						MySQLiteHelper.COLUMN_GPSGEOMID+" = "+insertId,
						null, null, null, null);
		cursor.moveToFirst();
		GpsGeom newGpsGeom = cursorToGpsGeom(cursor);//method at the end of the class
		cursor.close();
		return newGpsGeom;
	}
	
	/**
	 * convert the cursor to the object gpsGeom
	 * @param cursor
	 * @return
	 */
	  private GpsGeom cursorToGpsGeom(Cursor cursor) {
		    GpsGeom p1 = new GpsGeom();
		    p1.setGpsGeomId(cursor.getLong(0));
		    p1.setGpsGeomCoord(cursor.getString(1));
		    return p1;
			
		  }
	
}
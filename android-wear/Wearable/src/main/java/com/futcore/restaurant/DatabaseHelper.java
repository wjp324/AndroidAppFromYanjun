package com.futcore.restaurant;

import java.sql.SQLException;

import com.futcore.restaurant.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.futcore.restaurant.models.*;


/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "spiderman.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the ManCertPlace table
	private Dao<ManCertPlace, Integer> placeDao = null;
	private RuntimeExceptionDao<ManCertPlace, Integer> simpleRuntimeDao = null;

	private Dao<ManUser, Integer> userDao = null;
	private RuntimeExceptionDao<ManUser, Integer> userRuntimeDao = null;

	private Dao<ManEvent, String> eventDao = null;
	private RuntimeExceptionDao<ManEvent, String> eventRuntimeDao = null;

	private Dao<ManEventWear, String> eventWearDao = null;
	private RuntimeExceptionDao<ManEventWear, String> eventWearRuntimeDao = null;


	private Dao<ManItem, String> itemDao = null;
	private RuntimeExceptionDao<ManItem, String> itemRuntimeDao = null;

	private Dao<ManRecoItemWear, Integer> recoItemWearDao = null;
	private RuntimeExceptionDao<ManRecoItemWear, Integer> recoItemWearRuntimeDao = null;
    
	private Dao<ManRecoWrapper, Long> recoWrapperDao = null;
	private RuntimeExceptionDao<ManRecoWrapper, Long> recoWrapperRuntimeDao = null;
    

	public DatabaseHelper(Context context) {
        //		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, ManUser.class);
			TableUtils.createTable(connectionSource, ManItem.class);
			TableUtils.createTable(connectionSource, ManEvent.class);
			TableUtils.createTable(connectionSource, ManEventWear.class);
			TableUtils.createTable(connectionSource, ManCertPlace.class);
			TableUtils.createTable(connectionSource, ManRecoItemWear.class);

			TableUtils.createTable(connectionSource, ManRecoWrapper.class);
            
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		// here we try inserting data in the on-create as a test
        //		RuntimeExceptionDao<ManCertPlace, Integer> dao = getManCertPlaceDao();
        /*		long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		ManCertPlace simple = new ManCertPlace(millis);
		dao.create(simple);
		simple = new ManCertPlace(millis + 1);
		dao.create(simple);
		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
        */
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, ManUser.class, true);
			TableUtils.dropTable(connectionSource, ManCertPlace.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our ManCertPlace class. It will create it or just give the cached
	 * value.
	 */
	public Dao<ManCertPlace, Integer> getPlaceDao() throws SQLException {
		if (placeDao == null) {
			placeDao = getDao(ManCertPlace.class);
		}
		return placeDao;
	}

	public Dao<ManRecoItemWear, Integer> getRecoItemWearDao() throws SQLException {
		if (recoItemWearDao == null) {
			recoItemWearDao = getDao(ManRecoItemWear.class);
		}
		return recoItemWearDao;
	}
    
	public Dao<ManUser, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(ManUser.class);
		}
		return userDao;
	}

	public Dao<ManItem, String> getItemDao() throws SQLException {
		if (itemDao == null) {
			itemDao = getDao(ManItem.class);
		}
		return itemDao;
	}

	public Dao<ManEvent, String> getEventDao() throws SQLException {
		if (eventDao == null) {
			eventDao = getDao(ManEvent.class);
		}
		return eventDao;
	}

	public Dao<ManEventWear, String> getEventWearDao() throws SQLException {
		if (eventWearDao == null) {
			eventWearDao = getDao(ManEventWear.class);
		}
		return eventWearDao;
	}


	public Dao<ManRecoWrapper, Long> getRecoWrapperDao() throws SQLException {
		if (recoWrapperDao == null) {
			recoWrapperDao = getDao(ManRecoWrapper.class);
		}
		return recoWrapperDao;
	}
    

    public int clearAllUser() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManUser.class);
    }

    public int clearAllPlace() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManCertPlace.class);
    }

    public int clearAllEvent() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManEvent.class);
    }

    public int clearAllEventWear() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManEventWear.class);
    }

    public int clearAllRecoWrapper() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManRecoWrapper.class);
    }

    public int clearAllRecoItemWear() throws SQLException
    {
        return TableUtils.clearTable(connectionSource, ManRecoItemWear.class);
    }

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our ManCertPlace class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<ManCertPlace, Integer> getManCertPlaceDao() {
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(ManCertPlace.class);
		}
		return simpleRuntimeDao;
	}

	public RuntimeExceptionDao<ManUser, Integer> getManUserDao() {
		if (userRuntimeDao == null) {
			userRuntimeDao = getRuntimeExceptionDao(ManUser.class);
		}
		return userRuntimeDao;
	}

	public RuntimeExceptionDao<ManItem, String> getManItemDao() {
		if (itemRuntimeDao == null) {
			itemRuntimeDao = getRuntimeExceptionDao(ManItem.class);
		}
		return itemRuntimeDao;
	}

	public RuntimeExceptionDao<ManEvent, String> getManEventDao() {
		if (eventRuntimeDao == null) {
			eventRuntimeDao = getRuntimeExceptionDao(ManEvent.class);
		}
		return eventRuntimeDao;
	}

	public RuntimeExceptionDao<ManRecoItemWear, Integer> getManRecoItemWearDao() {
		if (recoItemWearRuntimeDao == null) {
			recoItemWearRuntimeDao = getRuntimeExceptionDao(ManRecoItemWear.class);
		}
		return recoItemWearRuntimeDao;
	}

	public RuntimeExceptionDao<ManEventWear, String> getManEventWearDao() {
		if (eventWearRuntimeDao == null) {
			eventWearRuntimeDao = getRuntimeExceptionDao(ManEventWear.class);
		}
		return eventWearRuntimeDao;
	}

	public RuntimeExceptionDao<ManRecoWrapper, Long> getManRecoWrapperDao() {
		if (recoWrapperRuntimeDao == null) {
			recoWrapperRuntimeDao = getRuntimeExceptionDao(ManRecoWrapper.class);
		}
		return recoWrapperRuntimeDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		placeDao = null;
        userDao = null;
        eventDao = null;
        eventWearDao = null;
        itemDao = null;
        recoItemWearDao = null;
        recoWrapperDao = null;
        
		simpleRuntimeDao = null;
        userRuntimeDao = null;
        eventRuntimeDao = null;
        eventWearRuntimeDao = null;
        itemRuntimeDao = null;
        recoItemWearRuntimeDao = null;
        recoWrapperRuntimeDao = null;
	}
}

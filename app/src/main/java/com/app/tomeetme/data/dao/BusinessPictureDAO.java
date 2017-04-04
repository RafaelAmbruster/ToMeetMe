package com.app.tomeetme.data.dao;

import com.app.tomeetme.data.AppDatabaseHelper;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.model.BusinessPictures;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BusinessPictureDAO extends AbstractDAO<BusinessPictures, String> implements
        IOperationDAO<BusinessPictures> {

    private AppDatabaseHelper helper;

    public BusinessPictureDAO(AppDatabaseHelper helper) {
        this.helper = helper;
    }

    public AppDatabaseHelper getHelper() {
        return helper;
    }

    @Override
    public Class<BusinessPictures> getEntityClass() {
        return BusinessPictures.class;
    }

    @Override
    public Dao<BusinessPictures, String> getDao() {
        try {
            return DaoManager.createDao(getHelper().getConnectionSource(), getEntityClass());
        } catch (Exception localException) {
            LogManager.getInstance().error(getClass().getCanonicalName(), localException.getMessage());
        }
        return null;
    }

    public void Create(BusinessPictures l, int operation) throws BusinessPictureException {
        try {
            switch (operation) {
                case IOperationDAO.OPERATION_INSERT:
                    getDao().create(l);
                    break;
                case IOperationDAO.OPERATION_INSERT_OR_UPDATE:
                    getDao().createOrUpdate(l);
                    break;
                case IOperationDAO.OPERATION_INSERT_IF_NOT_EXISTS:
                    getDao().createIfNotExists(l);
                    break;
            }
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public BusinessPictures Get(BusinessPictures object) throws BusinessPictureException {
        BusinessPictures BusinessPictures = null;
        try {
            BusinessPictures = getDao().queryForSameId(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
        return BusinessPictures;
    }

    public void Delete(BusinessPictures object) throws BusinessPictureException {
        try {
            getDao().delete(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public void Refresh(BusinessPictures object) throws BusinessPictureException {
        try {
            getDao().refresh(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public void Update(BusinessPictures object) throws BusinessPictureException {
        try {
            getDao().update(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public long CountOf() throws BusinessPictureException {
        long count = 0;
        try {
            count = getDao().countOf();
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
        return count;
    }

    public void Create(final ArrayList<BusinessPictures> list, final int operation) throws BusinessPictureException {
        try {
            getDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (BusinessPictures ur : list) {
                        Create(ur, operation);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            LogManager.getInstance().error("Inserting List ", e.getMessage());
        }
    }

    public ArrayList<BusinessPictures> Get() {
        ArrayList<BusinessPictures> lists = null;
        try {
            lists = (ArrayList<BusinessPictures>) getDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

}

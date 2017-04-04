package com.app.tomeetme.data.dao;

import com.app.tomeetme.data.AppDatabaseHelper;
import com.app.tomeetme.helper.log.LogManager;
import com.app.tomeetme.model.BusinessDaysSchedule;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class BusinessDaysScheduleDAO extends AbstractDAO<BusinessDaysSchedule, String> implements
        IOperationDAO<BusinessDaysSchedule> {

    private AppDatabaseHelper helper;

    public BusinessDaysScheduleDAO(AppDatabaseHelper helper) {
        this.helper = helper;
    }

    public AppDatabaseHelper getHelper() {
        return helper;
    }

    @Override
    public Class<BusinessDaysSchedule> getEntityClass() {
        return BusinessDaysSchedule.class;
    }

    @Override
    public Dao<BusinessDaysSchedule, String> getDao() {
        try {
            return DaoManager.createDao(getHelper().getConnectionSource(), getEntityClass());
        } catch (Exception localException) {
            LogManager.getInstance().error(getClass().getCanonicalName(), localException.getMessage());
        }
        return null;
    }

    public void Create(BusinessDaysSchedule l, int operation) throws BusinessDaysScheduleException {
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

    public BusinessDaysSchedule Get(BusinessDaysSchedule object) throws BusinessDaysScheduleException {
        BusinessDaysSchedule BusinessDaysSchedule = null;
        try {
            BusinessDaysSchedule = getDao().queryForSameId(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
        return BusinessDaysSchedule;
    }

    public void Delete(BusinessDaysSchedule object) throws BusinessDaysScheduleException {
        try {
            getDao().delete(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public void Refresh(BusinessDaysSchedule object) throws BusinessDaysScheduleException {
        try {
            getDao().refresh(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public void Update(BusinessDaysSchedule object) throws BusinessDaysScheduleException {
        try {
            getDao().update(object);
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
    }

    public long CountOf() throws BusinessDaysScheduleException {
        long count = 0;
        try {
            count = getDao().countOf();
        } catch (SQLException e) {
            LogManager.getInstance().error(getClass().getCanonicalName(), e.getMessage());
        }
        return count;
    }

    public void Create(final ArrayList<BusinessDaysSchedule> list, final int operation) throws BusinessDaysScheduleException {
        try {
            getDao().callBatchTasks(() -> {
                for (BusinessDaysSchedule ur : list) {
                    Create(ur, operation);
                }
                return null;
            });
        } catch (Exception e) {
            LogManager.getInstance().error("Inserting List ", e.getMessage());
        }
    }

    public ArrayList<BusinessDaysSchedule> Get() {
        ArrayList<BusinessDaysSchedule> lists = null;
        try {
            lists = (ArrayList<BusinessDaysSchedule>) getDao().queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lists;
    }

}

package com.app.tomeetme.data.dao;

import com.j256.ormlite.dao.Dao;

public abstract class AbstractDAO<T, E> {

    public abstract Class<T> getEntityClass();

    public abstract Dao<T, E> getDao();

}

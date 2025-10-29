package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Tax;

import java.util.Map;

public interface TaxDao {
    Map<String, Tax> getAllTaxes() throws DaoException;
    Tax getTax(String stateAbbrev) throws DaoException;
}

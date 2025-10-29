package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.model.Tax;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaxFileDao implements TaxDao{

    private final String taxFilePath;
    private Map<String, Tax> taxes;


    public TaxFileDao(String taxFilePath) {
        this.taxFilePath = taxFilePath;
    }


    private void loadIfNeeded() throws DaoException {
        if (taxes != null) return;
        taxes = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(taxFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("StateAbbrev")) continue;
                String[] parts = line.split(",");
                String abbr = parts[0];
                String name = parts[1];
                var rate = new BigDecimal(parts[2].trim());
                taxes.put(abbr, new Tax(abbr, name, rate));
            }
        } catch (Exception e) {
            throw new DaoException("Could not load taxes", e);
        }
    }

    @Override
    public Map<String, Tax> getAllTaxes() throws DaoException {
        loadIfNeeded();
        return Collections.unmodifiableMap(taxes);
    }

    @Override
    public Tax getTax(String stateAbbrev) throws DaoException {
        loadIfNeeded();
        return taxes.get(stateAbbrev);
    }
}

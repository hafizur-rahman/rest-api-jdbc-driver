package com.jdreamer.driver.web;

import com.google.common.collect.ImmutableMap;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.util.Source;
import org.apache.calcite.util.Sources;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WebDataSchema extends AbstractSchema {
    private final String url;
    private final String tenantId;
    private final String catalog;

    private Map<String, Table> tableMap;

    public WebDataSchema(String url, String tenantId, String catalog) {
        this.url = url;
        this.tenantId = tenantId;
        this.catalog = catalog;
    }

    @Override
    protected Map<String, Table> getTableMap() {
        if (tableMap == null) {
            tableMap = createTableMap();
        }
        return tableMap;
    }

    private Map<String, Table> createTableMap() {
        // Build a map from table name to table; each catalog becomes a table.
        final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();

        final String endpointUrl = String.format("%s/%s/catalog/%s", url, tenantId, catalog);

        try {
            Source source = Sources.of(new URL(endpointUrl));

            builder.put(catalog.toUpperCase(), new WebDataScannableTable(source, null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return builder.build();
    }
}

package com.jdreamer.driver.web;

import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.util.Map;

public class WebDataSchemaFactory implements SchemaFactory {

    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        final String url = (String) operand.get("url");
        final String tenantId = (String) operand.get("tenantId");
        final String catalog = (String) operand.get("catalog");

        return new WebDataSchema(url, tenantId, catalog);
    }
}

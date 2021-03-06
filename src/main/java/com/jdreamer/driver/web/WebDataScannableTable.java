package com.jdreamer.driver.web;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.file.CsvEnumerator;
import org.apache.calcite.adapter.file.CsvFieldType;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.util.ImmutableIntList;
import org.apache.calcite.util.Source;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

public class WebDataScannableTable extends CsvTable implements ScannableTable {

    /** Creates a WebDataScannableTable. */
    WebDataScannableTable(Source source, RelProtoDataType protoRowType) {
        super(source, protoRowType);
    }

    @Override
    public String toString() {
        return "WebDataScannableTable";
    }

    @Override
    public Enumerable<@Nullable Object[]> scan(DataContext root) {
        JavaTypeFactory typeFactory = requireNonNull(root.getTypeFactory(), "root.getTypeFactory");
        final List<CsvFieldType> fieldTypes = getFieldTypes(typeFactory);
        final List<Integer> fields = ImmutableIntList.identity(fieldTypes.size());
        final AtomicBoolean cancelFlag = DataContext.Variable.CANCEL_FLAG.get(root);
        return new AbstractEnumerable<@Nullable Object[]>() {
            @Override public Enumerator<@Nullable Object[]> enumerator() {
                return new CsvEnumerator<>(source, cancelFlag, false, null,
                        CsvEnumerator.arrayConverter(fieldTypes, fields, false));
            }
        };
    }
}

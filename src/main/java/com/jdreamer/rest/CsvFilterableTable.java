package com.jdreamer.rest;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.file.CsvEnumerator;
import org.apache.calcite.adapter.file.CsvFieldType;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.util.ImmutableIntList;
import org.apache.calcite.util.Source;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

/**
 * Table based on CsvProjectTableScanRule.java CSV file that can implement simple filtering.
 *
 * <p>It implements the {@link FilterableTable} interface, so Calcite gets
 * data by calling the {@link #scan(DataContext, List)} method.
 */
public class CsvFilterableTable extends CsvTable
        implements FilterableTable {
    /** Creates CsvProjectTableScanRule.java CsvFilterableTable. */
    public CsvFilterableTable(Source source, RelProtoDataType protoRowType) {
        super(source, protoRowType);
    }

    @Override public String toString() {
        return "CsvFilterableTable";
    }

    @Override public Enumerable<@Nullable Object[]> scan(DataContext root, List<RexNode> filters) {
        JavaTypeFactory typeFactory = requireNonNull(root.getTypeFactory(), "typeFactory");
        final List<CsvFieldType> fieldTypes = getFieldTypes(typeFactory);
        final @Nullable String[] filterValues = new String[fieldTypes.size()];
        filters.removeIf(filter -> addFilter(filter, filterValues));
        final List<Integer> fields = ImmutableIntList.identity(fieldTypes.size());
        final AtomicBoolean cancelFlag = DataContext.Variable.CANCEL_FLAG.get(root);
        return new AbstractEnumerable<@Nullable Object[]>() {
            @Override public Enumerator<@Nullable Object[]> enumerator() {
                return new CsvEnumerator<>(source, cancelFlag, false, filterValues,
                        CsvEnumerator.arrayConverter(fieldTypes, fields, false));
            }
        };
    }

    private static boolean addFilter(RexNode filter, @Nullable Object[] filterValues) {
        if (filter.isA(SqlKind.AND)) {
            // We cannot refine(remove) the operands of AND,
            // it will cause o.CsvProjectTableScanRule.java.c.i.TableScanNode.createFilterable filters check failed.
            ((RexCall) filter).getOperands().forEach(subFilter -> addFilter(subFilter, filterValues));
        } else if (filter.isA(SqlKind.EQUALS)) {
            final RexCall call = (RexCall) filter;
            RexNode left = call.getOperands().get(0);
            if (left.isA(SqlKind.CAST)) {
                left = ((RexCall) left).operands.get(0);
            }
            final RexNode right = call.getOperands().get(1);
            if (left instanceof RexInputRef
                    && right instanceof RexLiteral) {
                final int index = ((RexInputRef) left).getIndex();
                if (filterValues[index] == null) {
                    filterValues[index] = ((RexLiteral) right).getValue2().toString();
                    return true;
                }
            }
        }
        return false;
    }
}
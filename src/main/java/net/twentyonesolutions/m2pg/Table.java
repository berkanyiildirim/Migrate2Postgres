package net.twentyonesolutions.m2pg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table {

    final String schemaName;
    final String name;
    final List<Column> columns = new ArrayList();

    Column identity;

    public Table(String fullTableName) {

        int pos = fullTableName.indexOf('.');

        if (pos == -1){

            this.schemaName = "";
            this.name = fullTableName;
        }
        else {

            this.schemaName = fullTableName.substring(0, pos);
            this.name = fullTableName.substring(pos + 1);
        }
    }

    public String getColumnListSrc(Config config){

        String columnPrefix = (String)config.dml.getOrDefault("source_column_quote_prefix", "");
        String columnSuffix = (String)config.dml.getOrDefault("source_column_quote_suffix", "");

        String colsSql = this.columns.stream()
            .map(col -> columnPrefix + col.name + columnSuffix)
            .collect(
                    Collectors.joining(", ")
            );

        return colsSql;
    }


    public String getColumnListTgt(Config config){

//        String transformColumn = (String)config.config.getOrDefault("column_transform", "");

        String colsSql = this.columns.stream()
//            .map(col -> config.transform(col.name, transformColumn))
            .map(col -> config.getTargetColumnName(col.name))
            .collect(
                    Collectors.joining(", ")
            );

        return colsSql;
    }


    public StringBuilder getDdl(Config config){


        String colsSql = this.columns.stream()
            .map(col -> config.buildColumnDdlLine(col))
            .collect(
                    Collectors.joining("\n\t,")
            );

        StringBuilder ddl =new StringBuilder("CREATE TABLE " + config.getTargetTableName(this) + " (\n\t " + colsSql + "\n);");

        List<Constraints> constraintsArrayList;
        constraintsArrayList = Config.getAllConstraints();

        String srcTableName;
        String srcSchemaName;

        for(Constraints c:constraintsArrayList){
            srcSchemaName= c.table_view.split("\\.")[0];
            srcTableName= c.table_view.split("\\.")[1];

            c.table_view = config.getTargetTableName(srcTableName,srcSchemaName);
        }

//        ALTER TABLE table_name ADD PRIMARY KEY (column_1, column_2);
        String alter = "";
        for(Constraints c:constraintsArrayList){

            if(config.getTargetTableName(this).equals(c.table_view) && c.constraint_type.equals("Primary key") && c.object_type.equals("Table")){

                alter = "\nALTER TABLE " + c.table_view + " ADD PRIMARY KEY (" + config.getTargetColumnName(c.details) +");\n";
            }
        }
        ddl.append(alter);

        return ddl;
    }

    public boolean hasIdentity(){
        return identity != null;
    }

    public Column getIdentity() {
        return identity;
    }

    public void setIdentity(Column identity) {
        this.identity = identity;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.schemaName + "." + this.name;
    }
}

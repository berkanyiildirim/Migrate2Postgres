package net.twentyonesolutions.m2pg;

public class Constraints {

    String table_view, object_type, constraint_type, constraint_name, details;

    public Constraints(String table_view, String object_type, String constraint_type, String constraint_name, String details) {
        this.table_view = table_view;
        this.object_type = object_type;
        this.constraint_type = constraint_type;
        this.constraint_name = constraint_name;
        this.details = details;
    }


    @Override
    public String toString() {
        return "Constraints{" +
                "table_view='" + table_view + '\'' +
                ", object_type='" + object_type + '\'' +
                ", constraint_type='" + constraint_type + '\'' +
                ", constraint_name='" + constraint_name + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}


M2PG_CONF=MsSqlAWT2Postgres.conf

M2PG_CLASSPATH="target/classes:lib/*"

## get the command from the first arg passed, must be either DDL or DML
M2PG_COMMAND=$1

java -cp $M2PG_CLASSPATH net.twentyonesolutions.m2pg.PgMigrator $M2PG_COMMAND $M2PG_CONF

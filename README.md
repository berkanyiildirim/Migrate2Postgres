# Migrate2Postgres

Bu araç, veritabanlarını diğer JDBC uyumlu DBMS'lerden Postgres'e kolayca taşımanızı sağlar. Proje Java ile yazılmıştır, platform bağımsız Java SE Runtime sürüm 1.8 veya üzeri olan herhangi bir işletim sisteminde çalıştırılabilir.

Proje örnek olarak [SQL Server](src/main/resources/templates/ms-sql-server.conf) ile birlikte gelir, fakat diğer kaynak veritabanı sistemleri SQL Server ve [example config ](examples/conf) şablonlarında belgelenen kalıplar izlenerek kolayca eklenebilir.

# Gereksinimler

 - Java Runtime Environment (JRE) 1.8 or later
 - Kullanılan DBMS'ler için JDBC Sürücüleri (lib/ dizini altına eklendi)

# Başlangıç

Yapılandırma dosyasını oluşturun
--
Yapılandırma dosyası, geçiş için gereken bilgileri içeren bir JSON dosyasıdır.   Bağımsız bir dosya olabilir veya geçerli bir şablondan `template`  anahtarı belirterek devralınabilir, örn. [ms-sql-server](src/main/resources/templates/ms-sql-server.conf).

Bu bilgiler, kaynak ve hedef veritabanları için bağlantı ayrıntılarını, DDL aşaması için SQL türlerinin eşlemelerini (örn. SQL Server `NVARCHAR` Postgres `TEXT`), DML aşaması için JDBC türlerinin eşlemelerini, ad dönüşümlerini (örn. `SomeTableName` - `some_table_name`), thread sayısı ve daha fazlasını içerir.

"Etkili" yapılandırma değerleri aşağıdaki şekilde uygulanır:

1) `Varsayılanlar`  [defaults.conf](src/main/resources/templates/defaults.conf) dosyasından okunur.
2) Yapılandırma dosyasının `template` adlı bir anahtarı varsa, değerde belirtilen şablon okunur.
3) Config dosyasındaki değerler ayarlanır
4) `%` Simgesiyle sarılmış değerler diğer yapılandırma ayarlarından veya Java sistem özelliklerinden değerlendirilir.

Template dosyalarındaki anahtarlarla eşleşen configuration dosyası anahtarları template ayarlarını geçersiz kılar. örneğin, yapılandırma dosyası `dml.threads` anahtarını `4` değeriyle belirtirse, `defaults` şablonunda belirtilen ve "cores" olarak ayarlanan ayarın üzerine yazılır. (çekirdekler, JVM tarafından kullanılabilen CPU çekirdeklerinin sayısı ifade eder).

`%` Simgesiyle sarılmış değerler değişken olarak varsayılır ve çalışma zamanında değerlendirilir. Değişken değerlerini config dosyasında veya Java Sistem Özellikleri olarak ayarlanabilir. Örneğin, "source.db_name" anahtarına "AdventureWorks" değerini iki yoldan biriyle belirtebilirsiniz:

1) Config dosyasında aşağıdaki gibi ayarlayarak:

    `source : {
        db_name : "AdventureWorks"
    }`

2) JVM argümanları aracılığıyla `<options>` içinde bir Java Sistem Özelliği ayarlayarak:

    `-Dsource.db_name=AdventureWorks`

Böylece `%source.db_name%` yapılandırma değeri çalışma zamanında "AdventureWorks" olarak değerlendirilir. Aynı anahtar hem yapılandırma dosyasında hem de Java Sistem Özellikleri'nde belirtilirse, Java Sistem Özellikleri kullanılır.

Daha fazla bilgi için [defaults.conf](src/main/resources/templates/defaults.conf),  [template for SQL Server](src/main/resources/templates/ms-sql-server.conf) ve [örnek yapılandırma dosyalarındaki](examples/conf) yorumlara bakın.

DDL komutunu çalıştırın
--
Çalışma dizininde `CREATE SCHEMA`, `CREATE TABLE` vb. komutları içeren SQL script dosyası oluşturur.

Oluşturulan DDL komut dosyasını çalıştırın
--
Önceki adımda oluşturulan komut dosyasını gözden geçirin ve gerekirse değişiklik yapın, ardından psql, PgAdmin veya DBeaver gibi SQL istemcisinde.

Veya DDLEXECUTE komutunu çalıştırın
--
Çalışma dizininde `CREATE SCHEMA`, `CREATE TABLE` vb. komutları içeren SQL script dosyası oluşturur ve oluşturulan komut dosyasını kopyala yapıştır veya SQL istemcisi olmadan otomatik olarak çalıştırır. Oluşturulan komut dosyasını gözden geçirmeniz gerekmiyorsa kullanabilirsiniz.

DML komutunu çalıştırın
--
Bu, yapılandırma dosyasındaki ayarlara göre verileri kaynak veritabanındaki hedef Postgres veritabanınıza kopyalar.

Tatile çıkın
--
Muhtemelen haftalarca sürecek çalışmayı birkaç saate hallettin. Bence bir tatili hak ediyorsun!

# Watch tutorial video

[![Migrate a SQL Server Database to Postgres](http://img.youtube.com/vi/5eF9_UB73TI/0.jpg)](http://www.youtube.com/watch?v=5eF9_UB73TI "How to Easily Migrate a SQL Server Database to Postgres")

# Kullanımı:

    java <options> net.twentyonesolutions.m2pg.PgMigrator <command> [<config-file> [<output-file>]]

Örnek çalıştırma:

    ./migrate.sh ddl  #veya  ./migrate.sh ddlexecute 
    ./migrate.sh dml    

  `<options>`
--
İhtiyaç olursa JVM (Java) seçenekleri, `classpath` ve memory ayarları gibi.

Bazı yapılandırma değerlerini yapılandırma dosyasında tutmak istemeyebileceğiniz için `<options>` de aktarabilirsiniz;
    -Dsqlserver.username=pgmigrator -Dsqlserver.password=secret
    
Böylece config dosyasını aşağıdaki gibi refer edebilirsiniz:

    connections : {
        mssql : {
             user     : "%sqlserver.username%"
            ,password : "%sqlserver.password%"
            // rest ommitted for clarity
    }

  `<command>`
--
 - `DDL` - Eşlenen data types, name transformations, identity columns vb. ile schemayı oluşturacak SQL script dosyası oluşturur. Komut dosyasını tercih ettiğiniz SQL istemcisiyle çalıştırmadan önce gözden geçirmelisiniz.
 
 - `DDLEXECUTE` - Eşlenen data types, name transformations, identity columns vb. ile schemayı oluşturacak SQL script dosyasını oluşturur ve otomatik çalıştırır. SQL istemcisine veya kopyala-yapıştır işlemine ihtiyacınız olmaz.
  
 - `DML` - `DDL` adımında kaynak veritabanından oluşturulan schemayı hedef Postgres veritabanına kopyalar.

  `<config-file>`
-- Yapılandırma dosyasının isteğe bağlı yolunu tanımlar. Varsayılan `./Migrate2Postgres.conf`.

  `<output-file>`
--
output/log dosyaları için path'ı tanımlar. Vasayılan, proje ismi+timestamp olarak çalışma dizinidir. The arguments are passed by position, so `<output-file>` can only be passed if `<config-file>` was passed explicitly.

Ayrıca bakınız [shell/batch example scripts](examples/bin)

# Config File Reference  (WIP)

Config dosyası JSON formatındadır ve Migration Projesinin ayrıntılarını içerir.

Çalışma zamanında, önce defaults.conf dosyası okunur, ardından projenin yapılandırma dosyasında bir şablon belirtilirse değerleri uygulanır ve sonra projenin yapılandırma dosyasındaki ayarlar uygulanır. Aynı anahtar yoluna sahip ayarlar, aynı yolun önceki değerlerinin üzerine yazılır.

Bir JSON dosyası olarak ters eğik çizgilerden kaçınılmalıdır, bu nedenle `"a\b"`  dizesini koymak istiyorsanız ters eğik çizgiden kaçmalı ve `"a\\b"`' olarak yazmalısınız.

Values that are wrapped in `%` symbols are treated as varaibles and evaluated at runtime, so for example if you specify a value of `%sqlserver.password%`, the tool will look for a value with that key either in the JVM System Properties, or the config files, and replace the variable with that value.

 
`%` Sembolü ile sarılmış değerler, değişken olarak değerlendirilir ve  runtime sırasında değerlendirilir; örneğin, `%sqlserver.password%` değerini belirtirseniz, tool bu anahtarla JVM Sistem Özellikleri'nde veya yapılandırma dosyalarında bir değer arar ve değişkeni bu değerle değiştirir.

```
*
|
+-- name                              string - name of migration project, used as prefix in logs etc.
|
+-- template                          string - a template to be used, e.g. "ms-sql-server"
|
+-- source                            string - the key from connections that will be used as the source connection
|
+-- target                            string - the key from connections that will be used as the target connection
|
+-- connections                       struct - key is the connection name, value is a struct with at least connectionString, user, password
|
+-- information_schema
    |
    +-- query                         string - SQL query that will return all of the tables and columns to be migrated
    |
    +-- database_name                 string - used in the information_schema.query to specify the source database
|
+-- schema_mapping                    struct - maps schema names if needed, e.g. "dbo" -> "public"
|
+-- table_mapping                     struct - maps table names if needed, e.g. "SomeVeryLongTableName" -> "a_table_name"
|
+-- column_mapping                    struct - maps column names if needed, e.g. "group" -> "group_name"
|
+-- table_transform                   string - ([""], "lower_case", "upper_case", "camel_to_snake_case")
|
+-- column_transform                  string - ([""], "lower_case", "upper_case", "camel_to_snake_case")
|
+-- ddl
    |
    +-- drop_schema                   ([false]|true) - whether to add DROP SCHEMA IF EXISTS before each schema
    |
    +-- sql_type_mapping              struct - maps SQL data types, e.g. DATETIME -> TIMESTAMPTZ, IMAGE -> BYTEA, etc.
    |
    +-- column_default_replace        struct - maps DEFAULT column values by using REGular EXpressions
|
+-- dml
    |
    +-- execute
        |
        +-- before_all                array of SQL commands to run before data copy
        |
        +-- after_all                 array of SQL commands to run after data copy
        |
        +-- recomended                ([""], "all") - specifying "all" will execute recommendations
    |
    +-- threads                       (["cores", integer]) - number of concurrent connections
    |
    +-- on_error                      string - (["rollback"])
    |
    +-- jdbc_type_mapping             struct - maps nonstandard JDBC types during data copy
    |
    +-- source_column_quote_prefix    string - a prefix for quoting columns, e.g. `[` in SQL Server
    |
    +-- source_column_quote_suffix    string - a suffix for quoting columns, e.g. `]` in SQL Server
```

`name`
--
Migration projesinin adını gösterir. Çıktı dosyalarının önüne bu ad gelir.

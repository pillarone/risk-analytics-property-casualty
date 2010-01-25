dataSource {
    pooled = false
    driverClassName = "org.hsqldb.jdbcDriver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
    // additional to standard
//    c3p0.initialPoolSize = 5
//    c3p0.acquire_increment = 5
//    c3p0.min_size = 5
//    c3p0.max_size = 20
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:file:devRiskAnalyticsDb;shutdown=true"
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:mem:devDB"
        }
    }
    trunksandbox {
        dataSource {
            pooled = true
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/riskAnalyticsCI"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "p1rat"
            password = "p1rat"
        }
    }
    production {
        dataSource {
            pooled = true
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/p1rat"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "p1rat"
            password = "p1rat"
        }
    }
    mysql {
        dataSource {
            // Setting up mysql:
            //   create database p1rat;
            //   create user 'p1rat'@'localhost' identified by 'p1rat';
            //   grant all on table p1rat.* to 'p1rat'@'localhost';
            // required for batch uploads:
            //   grant file on *.* to 'p1rat'@'localhost';
            dbCreate = "update" // should always stay on update! use InitDatabase script to drop/create DB
            url = "jdbc:mysql://localhost/p1rat"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5Dialect"
            username = "p1rat"
            password = "p1rat"
            pooled = true
        }
    }

    sqlserver {
        dataSource {
            dbCreate = "update" // should always stay on update! use InitDatabase script to drop/create DB
            url = "jdbc:jtds:sqlserver://localhost/p1rat"
            driverClassName = "net.sourceforge.jtds.jdbc.Driver"
            dialect = "org.hibernate.dialect.SQLServerDialect"
            username = "sa"
            password = "admin"
            pooled = true
        }
    }

    standalone {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop','update'
            // the database has to be created in a directory writable by the user
            url = "jdbc:derby:${System.getProperty('user.home')}/PillarOne/riskAnalytics/riskAnalyticsDB-0-4-2;create=true"
            driverClassName = "org.apache.derby.jdbc.EmbeddedDriver"
            dialect = "org.hibernate.dialect.DerbyDialect"
        }
    }
}
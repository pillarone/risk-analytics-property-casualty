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
            dbCreate = "create-drop"
            driverClassName = "org.h2.Driver"
            url = "jdbc:h2:mem:devDB"
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:hsqldb:mem:devDB"
        }
    }
    production {
        dataSource {
            dbCreate = "create-drop"
            driverClassName = "org.h2.Driver"
            url = "jdbc:h2:mem:prodDB"
        }
    }
}
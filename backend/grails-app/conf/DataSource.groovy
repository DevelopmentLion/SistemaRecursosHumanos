dataSource {
    pooled = true
    driverClassName = "com.mysql.cj.jdbc.Driver"
    username = "root"
    password = "C14R1741"
}

hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory'
}

environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://172.16.1.202:3306/PruebaEver?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowPublicKeyRetrieval=true"
            properties {
                maximumPoolSize = 80
                minimumIdle = 10
                connectionTestQuery = "SELECT 1"
                cachePrepStmts = true
                prepStmtCacheSize = 250
                prepStmtCacheSqlLimit = 2048
                useServerPrepStmts = true
            }
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://127.0.0.1/secah2013?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true"
            username = "test"
            password = "testpw"
        }
    }
    production {
        dataSource {
            pooled = true
            dbCreate = "update"
            url = "jdbc:mysql://172.16.1.202/secah2014?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true"
            username = "root"
            password = "C14R1741"
            properties {
                maximumPoolSize = 80
                minimumIdle = 10
                connectionTestQuery = "SELECT 1"
            }
        }
    }
}

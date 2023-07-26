package hygge.blog.config.database;

import com.zaxxer.hikari.HikariDataSource;
import hygge.commons.spring.config.configuration.definition.HyggeAutoConfiguration;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MySQL57Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * @author Xavier
 * @date 2022/7/17
 */
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "webAppEntityManagerFactory",
        transactionManagerRef = "webAppTransactionManager",
        basePackages = {"hygge.blog.repository"}
)
@Configuration
@EnableConfigurationProperties(DatabaseConfiguration.class)
public class DataBaseAutoConfig implements HyggeAutoConfiguration {
    @Autowired
    private DatabaseConfiguration databaseConfiguration;

    @Bean("webAppDataSource")
    public HikariDataSource webAppDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(databaseConfiguration.getUserName());
        hikariDataSource.setPassword(databaseConfiguration.getPassword());
        hikariDataSource.setJdbcUrl(databaseConfiguration.getUrl());
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        hikariDataSource.setMaximumPoolSize(20);
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaxLifetime(35000);
        hikariDataSource.setIdleTimeout(30000);
        return hikariDataSource;
    }

    @Bean("webAppEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(@Qualifier("webAppDataSource") HikariDataSource hikariDataSource) {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        // 显示执行语句
        hibernateJpaVendorAdapter.setShowSql(databaseConfiguration.getShowSql());
        // 自动根据实体建表
        hibernateJpaVendorAdapter.setGenerateDdl(databaseConfiguration.getAutoGenerateDdl());
        // 设置数据库类型
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        // 设置数据库方言 (此参数的 key 实际上是 "AvailableSettings.DIALECT" 其 value 可选项参见 "org.hibernate.dialect" 这个包下的类)
        // 事实上这个参数非必填，会自动根据数据库类型识别，JPA 当前版本中， Mysql 数据库默认值是 "org.hibernate.dialect.MySQL57Dialect"
        hibernateJpaVendorAdapter.setDatabasePlatform(MySQL57Dialect.class.getName());

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(hikariDataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("hygge.blog.domain.local.po");
        // 当前线程不存在事务、事务已关闭时，也允许懒加载查询(异步线程时通常用得上)
        Map<String, Object> jpaPropertyMap = entityManagerFactoryBean.getJpaPropertyMap();
        jpaPropertyMap.put(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, "true");
        jpaPropertyMap.put(AvailableSettings.FORMAT_SQL, "true");
        jpaPropertyMap.put(AvailableSettings.HIGHLIGHT_SQL, "true");
        return entityManagerFactoryBean;
    }

    @Bean("webAppTransactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }
}

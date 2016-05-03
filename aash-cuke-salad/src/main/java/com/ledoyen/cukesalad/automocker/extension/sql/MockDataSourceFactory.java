package com.ledoyen.cukesalad.automocker.extension.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.ledoyen.cukesalad.automocker.api.Resetable;

public class MockDataSourceFactory implements FactoryBean<DataSource>, BeanNameAware, InitializingBean, Resetable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockDataSourceFactory.class);

	private String beanName;
	private JdbcDataSource instance;

	@Override
	public DataSource getObject() throws Exception {
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = new JdbcDataSource();
		instance.setUrl("jdbc:h2:mem:" + beanName + ";DB_CLOSE_DELAY=-1");
		LOGGER.debug("Mocking up " + DataSource.class.getSimpleName() + " of name " + beanName);
	}

	@Override
	public void reset() {
		try (Connection c = instance.getConnection();
				PreparedStatement ps = c.prepareStatement("SHOW TABLES");
				ResultSet rs = ps.executeQuery();) {
			Connections.tables(c).forEach(table -> {
				try {
					c.prepareStatement("TRUNCATE TABLE " + table).execute();
				} catch (SQLException e) {
					throw new IllegalStateException("Could not reset DB[" + beanName + "]");
				}
			});
		} catch (SQLException e) {
			throw new IllegalStateException("Could not reset DB[" + beanName + "]");
		}
	}
}

package net.muratov;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
@ComponentScan("net.muratov")
public class DBWriterApplication {

	@Value("${spring.datasource.driverClassName}")
	private String driver;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;

	@Bean
	public DataSource configureDataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driver);
		bds.setUsername(username);
		bds.setPassword(password);
		bds.setUrl(url);
        bds.setValidationQuery("select 1");
		return bds;
	}

	public static void main(String[] args) {
		SpringApplication.run(DBWriterApplication.class, args);
	}
}

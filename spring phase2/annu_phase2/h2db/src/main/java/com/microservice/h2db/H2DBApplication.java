package com.microservice.h2db;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class H2DBApplication {
	@Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9082");
    }

	public static void main(String[] args) {
		SpringApplication.run(H2DBApplication.class, args);
	}



@Bean
public Server h2ConsoleServer() throws SQLException {
    return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083");
}
}



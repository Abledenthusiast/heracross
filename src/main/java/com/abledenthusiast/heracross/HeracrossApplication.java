package com.abledenthusiast.heracross;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeracrossApplication {

  public static void main(String[] args) {
    try{
      Connection connection = DriverManager.getConnection("jdbc:derby:testdb1;create=true");
    } catch(Exception exc) {
      exc.printStackTrace();
    }
    SpringApplication.run(HeracrossApplication.class, args);
  }

}

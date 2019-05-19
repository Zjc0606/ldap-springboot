package com.ruhul.study;

import com.ruhul.study.dao.PersonDao;
import com.ruhul.study.dao.PersonDaoImpl;
import com.ruhul.study.model.Person;
import com.ruhul.study.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
public class LdapApplication {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PersonDao dao;

    private static Logger log = LoggerFactory.getLogger(LdapApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(LdapApplication.class, args);
    }

    @PostConstruct
    public void setup(){
        log.info("Spring LDAP + Spring Boot Configuration Example");

        List<String> names = personRepository.getAllPersonNames();
        log.info("names: " + names);
        Person p = new Person();
        p.setFullName("test");
        p.setLastName("tests");
        p.setCompany("hcl");
        p.setCountry("india");
        dao.create(p);
        dao.findAll().forEach(x -> System.out.println(x));

        System.exit(-1);
    }
}

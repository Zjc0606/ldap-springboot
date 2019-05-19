package com.ruhul.study.dao;

import com.ruhul.study.model.Person;

import java.util.List;

public interface PersonDao {

    void create(Person person);

    void update(Person person);

    void delete(Person person);

    Person findByPrimaryKey(String name, String company, String country);

    List findByName(String name);

    List findAll();
}

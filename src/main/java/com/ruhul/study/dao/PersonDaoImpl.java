package com.ruhul.study.dao;

import java.util.List;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.ruhul.study.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.*;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.stereotype.Service;

@Service
public class PersonDaoImpl implements PersonDao {

    @Autowired
    private LdapTemplate ldapTemplate;

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public void create(Person person) {
        DirContextAdapter context = new DirContextAdapter(buildDn(person));
        mapToContext(person, context);
        ldapTemplate.bind(context);
    }

    @Override
    public void update(Person person) {
        Name dn = buildDn(person);
        DirContextOperations context = ldapTemplate.lookupContext(dn);
        mapToContext(person, context);
        ldapTemplate.modifyAttributes(context);
    }

    @Override
    public void delete(Person person) {
        ldapTemplate.unbind(buildDn(person));
    }

    @Override
    public Person findByPrimaryKey(String name, String company, String country) {
        Name dn = buildDn(name, company, country);
        return (Person) ldapTemplate.lookup(dn, getContextMapper());
    }

    @Override
    public List findByName(String name) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person")).and(new WhitespaceWildcardsFilter("cn",name));
        return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
    }

    @Override
    public List findAll() {
        EqualsFilter filter = new EqualsFilter("objectclass", "person");
        return ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), getContextMapper());
    }

    protected ContextMapper getContextMapper() {
        return new PersonContextMapper();
    }

    protected Name buildDn(Person person) {
        return buildDn(person.getFullName(), person.getCompany(), person.getCountry());
    }

    protected Name buildDn(String fullname, String company, String country) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("dc", "com");
        dn.add("dc", "hcl");
        dn.add("ou", "people");
        dn.add("cn", fullname);
        return dn;
    }

    protected void mapToContext(Person person, DirContextOperations context) {
        context.setAttributeValues("objectclass", new String[] {"top", "person"});
        context.setAttributeValue("cn", person.getFullName());
        context.setAttributeValue("sn", person.getLastName());
        context.setAttributeValue("description", person.getDescription());
    }

    private static class PersonContextMapper extends AbstractContextMapper {
        public Object doMapFromContext(DirContextOperations context) {

//            NamingEnumeration<? extends Attribute> namingEnumeration = context.getAttributes().getAll();
//            while (namingEnumeration.hasMoreElements()){
//                Attribute attribute = null;
//                try {
//                    attribute = namingEnumeration.next();
//                } catch (NamingException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(attribute);
//            }
            Person person = new Person();
            person.setFullName(context.getStringAttribute("cn"));
            person.setLastName(context.getStringAttribute("sn"));
           // person.setCountry(context.getStringAttribute("c"));
           // person.setCompany(context.getStringAttribute("ou"));
            person.setDescription(context.getStringAttribute("description"));
            return person;
        }
    }
}
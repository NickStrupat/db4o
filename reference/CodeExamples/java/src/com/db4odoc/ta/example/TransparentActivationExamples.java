package com.db4odoc.ta.example;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.ta.TransparentActivationSupport;
import com.db4o.ta.TransparentPersistenceSupport;

import java.io.File;


public class TransparentActivationExamples {
    private static final String DATABASE_FILE_NAME = "database.db4o";

    public static void main(String[] args) {
        transparentActivationExample();
        transparentPersistenceExample();

    }

    private static void transparentActivationExample() {
        cleanUp();
        // #example: Transparent activation in action
        {
            ObjectContainer container = openDatabaseWithTA();
            Person person = Person.personWithHistory();
            container.store(person);
            container.close();
        }
        {
            ObjectContainer container = openDatabaseWithTA();
            Person person = queryByName(container,"Joanna the 10");
            Person beginOfDynasty = person.getMother();

            // With transparent activation enabled, you can navigate deeply
            // nested object graphs. db4o will ensure that the objects
            // are loaded from the database.
            while(null!=beginOfDynasty.getMother()){
                beginOfDynasty = beginOfDynasty.getMother();
            }
            System.out.println(beginOfDynasty.getName());
            
            container.close();
        }
        // #end example

        cleanUp();
    }

    private static void transparentPersistenceExample() {
        cleanUp();
        // #example: Transparent persistence in action
        {
            ObjectContainer container = openDatabaseWithTP();
            Person person = Person.personWithHistory();
            container.store(person);
            container.close();
        }
        {
            ObjectContainer container = openDatabaseWithTP();
            Person person = queryByName(container,"Joanna the 10");
            Person beginOfDynasty = person.getMother();

            // With transparent persistence enabled, you can navigate deeply
            // nested object graphs. db4o will ensure that the objects
            // are loaded from the database.
            while(null!=beginOfDynasty.getMother()){
                beginOfDynasty = beginOfDynasty.getMother();
            }
            System.out.println(beginOfDynasty.getName());

            // Updating a object doesn't requires no store call.
            // Just change the objects and the call commit.
            beginOfDynasty.setName("New Name");
            container.commit();
            container.close();
        }
        {
            ObjectContainer container = openDatabaseWithTP();
            Person person = queryByName(container,"New Name");
            // The changes are stored, due to transparent persistence
            System.out.println(person.getName());
            container.close();
        }
        // #end example

        cleanUp();
    }

    private static void cleanUp() {
        new File(DATABASE_FILE_NAME).delete();
    }

    private static Person queryByName(ObjectContainer container,final String name) {
        return container.query(new Predicate<Person>() {
            @Override
            public boolean match(Person o) {
                return o.getName().equals(name);
            }
        }).get(0);
    }

    private static ObjectContainer openDatabaseWithTP() {
        // #example: Activate transparent persistence
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new TransparentPersistenceSupport());
        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE_NAME);
        // #end example
        return container;
    }

    private static ObjectContainer openDatabaseWithTA() {
        // #example: Activate transparent activation
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new TransparentActivationSupport());
        ObjectContainer container = Db4oEmbedded.openFile(configuration, DATABASE_FILE_NAME);
        // #end example
        return container;
    }
}

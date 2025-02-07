package library.persistence;

/*
 ** DO NOT CHANGE!!
 */


import library.model.Person;

import java.util.Optional;

public interface PersonDAO {
    Optional<Person> findPersonByEmail(String email);
    Person savePerson(Person person);
}

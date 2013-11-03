package workflow.event;

import java.io.Serializable;

import workflow.domain.Person;

public class PersonChanged extends DatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "PersonChanged [getPerson()=" + getPerson() + ", getDate()=" + getDate() + "]";
    }
}

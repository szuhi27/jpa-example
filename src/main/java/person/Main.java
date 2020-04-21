package person;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


@Log4j2
public class Main {

    public static Faker faker = new Faker();
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");

    private static Person randomPerson() {
        Person person = new Person();
        person.setName(faker.name().name());
        Date date = faker.date().birthday();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        person.setDob(localDate);
        person.setGender(faker.options().option(Person.Gender.class));

        Address adress = new Address();
            adress.setCountry(faker.address().country());
            adress.setState(faker.address().state());
            adress.setCity(faker.address().city());
            adress.setStreetAddress(faker.address().streetAddress());
            adress.setZip(faker.address().zipCode());

        person.setAddress(adress);
        person.setEmail(faker.internet().emailAddress());
        person.setProfession(faker.company().profession());
        return person;
    }

    public static void main(String[] args) {

        EntityManager em = emf.createEntityManager();

        int people = 1000;
        try {
            em.getTransaction().begin();
            for (int i=0; i<people; ++i) {
                em.persist(randomPerson());
            }
            em.getTransaction().commit();
            em.createQuery("select l from Person l order by l.id", Person.class).getResultList().forEach(log::info);
        }
        finally {
            em.close();
            emf.close();
        }
    }
}


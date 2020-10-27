package socialnetwork.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilizatorTest {

    private Utilizator u1;
    private Utilizator u2;

    @BeforeEach
    public void setUp()  {
        u1 = new Utilizator("Ana","Maria");
        u2 = new Utilizator("Geo","Leo");
    }

    @Test
    @Tag("t")
    void getFirstName() {
        assert u1.getFirstName() == "Ana";
        assertEquals("Geo", u2.getFirstName());
    }

    @Test
    @Tag("t")
    void setFirstName() {
        u1.setFirstName("Vasile");
        assertEquals("Vasile", u1.getFirstName());
    }

}
package socialnetwork.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {
    private Entity<Long> fb;

    @BeforeEach
    public void setUp()  {
        fb = new Entity<Long>();
    }

}
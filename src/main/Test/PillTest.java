import com.example.pillandcapsuleanalyser.Pill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PillTest {
    Pill p1, p2, p3;

    @BeforeEach
    void setup(){
        p1 = new Pill(200, "Vitamin B12", 1, 100, 10, 40, 0);
        p2 = new Pill(390, "Paracetamol", 2, 204, 100, 40, 1);
    }

    @Test
    void testGetters(){
        assertEquals("Vitamin B12", p1.getName());
        assertEquals("Paracetamol", p2.getName());
        assertEquals(0, p1.getType());
        assertEquals(390, p2.getRoot());
    }

    @Test
    void testSetters(){
        assertEquals(1, p1.getNum());
        p1.setNum(3);
        assertEquals(3, p1.getNum());
    }
}

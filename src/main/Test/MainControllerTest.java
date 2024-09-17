import com.example.pillandcapsuleanalyser.MainController;
import com.example.pillandcapsuleanalyser.Pill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainControllerTest {
    MainController mc;
    Pill p, p1;

    @BeforeEach
    void setup(){
        mc = new MainController();
        p = new Pill(10, "Pill", 12, 200, 12, 12, 0);
        p1 = new Pill(20, "Pill2", 12, 200, 12, 12, 0);
        mc.getAllPills().add(p);
        mc.getAllPills().add(p1);
    }

    @Test
    void testGetAllPills(){
        assertTrue(mc.getAllPills().contains(p));
        assertTrue(mc.getAllPills().contains(p1));
    }

    @Test
    void testGetPillAtRoot(){
        assertEquals(p, mc.getPillAtRoot(10));
        assertEquals(p1, mc.getPillAtRoot(20));
    }
}

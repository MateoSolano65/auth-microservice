package co.com.pragma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainApplicationTest {
    @Test
    void mainMethodRuns() {
        assertDoesNotThrow(() -> {
            MainApplication.main(new String[] { "--server.port=0" });
        });
    }
}

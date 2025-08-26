package co.com.pragma.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        // @Bean
        // public UserRepository userRepository() {
        //     return UserRepository();
        // }
    }

    // @Configuration
    // public class R2dbcTxTestConfig {

    //   @Bean
    //   public R2dbcTransactionManager r2dbcTransactionManager(io.r2dbc.spi.ConnectionFactory cf) {
    //     return new R2dbcTransactionManager(cf);
    //   }
    // }


    // static class MyUseCase {
    //     public String execute() {
    //         return "MyUseCase Test";
    //     }
    // }
}
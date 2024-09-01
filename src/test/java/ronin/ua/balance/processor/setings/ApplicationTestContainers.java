package ronin.ua.balance.processor.setings;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class ApplicationTestContainers {


	static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("test")
			.withUsername("user")
			.withPassword("password");

	static {
		postgresContainer.start();
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		@Override
		public void initialize(ConfigurableApplicationContext context) {
			TestPropertyValues.of(
					"spring.datasource.url=" + postgresContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgresContainer.getUsername(),
					"spring.datasource.password=" + postgresContainer.getPassword()
			).applyTo(context.getEnvironment());
		}
	}


}

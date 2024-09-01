package ronin.ua.balance.processor.setings;

import org.springframework.boot.SpringApplication;
import ronin.ua.balance.processor.Application;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}

package pl.piotr.java_shop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
class JavaShopApplicationTests {

	@Test
	void contextLoads() {
	}

}

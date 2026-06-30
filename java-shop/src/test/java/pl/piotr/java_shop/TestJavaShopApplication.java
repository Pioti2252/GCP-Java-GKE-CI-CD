package pl.piotr.java_shop;

import org.springframework.boot.SpringApplication;

public class TestJavaShopApplication {

	public static void main(String[] args) {
		SpringApplication.from(JavaShopApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

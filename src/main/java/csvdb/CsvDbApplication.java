package csvdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

@SpringBootApplication
public class CsvDbApplication {
	public static final File HOME;

	static {
		HOME = new ApplicationHome(CsvDbApplication.class).getDir();
		System.setProperty("HOME", HOME.getAbsolutePath());
	}


	public static void main(String[] args) {
		SpringApplication.run(CsvDbApplication.class, args);
	}

}

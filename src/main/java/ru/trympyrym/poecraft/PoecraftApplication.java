package ru.trympyrym.poecraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import ru.trympyrym.poecraft.model.Affix;
import ru.trympyrym.poecraft.model.AffixType;
import ru.trympyrym.poecraft.storage.DataLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
public class PoecraftApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoecraftApplication.class, args);
	}

	@Bean
	public DataLoader dataLoader() throws IOException {
		InputStream resourceStream = new ClassPathResource("affixes").getInputStream();
		try ( BufferedReader reader = new BufferedReader(
				new InputStreamReader(resourceStream)) ) {
			List<Affix> data = reader.lines()
					.map(line-> {
						String[] splitted = line.split(";");
						return new Affix(
								UUID.fromString(splitted[0]),
								"prefix".equals(splitted[3]) ? AffixType.PREFIX : AffixType.SUFFIX,
								Integer.parseInt(splitted[5]),
								splitted[6].replace("<br/>", ";")
						);
					}).collect(Collectors.toList());
			return new DataLoader(data);
		}
	}

}

package com.example.insurance;

// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
// import java.util.Base64;
// import javax.crypto.SecretKey;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Encoders;
// import io.jsonwebtoken.security.Keys;

@EnableJpaAuditing
@SpringBootApplication
public class InsuranceApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SpringApplication.run(InsuranceApplication.class, args);

		// // Generate key pair
		// KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
		// kpg.initialize(256); // EC size (256 = prime256v1)
		// KeyPair keyPair = kpg.generateKeyPair();

		// // Get keys in Base64
		// String privateKey = Base64.getEncoder().encodeToString(
		// keyPair.getPrivate().getEncoded());

		// String publicKey = Base64.getEncoder().encodeToString(
		// keyPair.getPublic().getEncoded());

		// // Print for copy/paste to application.properties
		// System.out.println("jwt.privateKey=" + privateKey);
		// System.out.println("jwt.publicKey=" + publicKey);
	}

}

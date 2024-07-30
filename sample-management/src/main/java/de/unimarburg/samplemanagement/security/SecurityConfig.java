package de.unimarburg.samplemanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests)->requests
                        .anyRequest().permitAll());
        return http.build();
    }
//
//    @Bean
//    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() throws CertificateException {
//        X509Certificate idpCertificate = loadCertificate("classpath:idp-certificate.pem");
//
//        Saml2X509Credential idpVerificationCredential = new Saml2X509Credential(idpCertificate,
//                Saml2X509Credential.Saml2X509CredentialType.VERIFICATION);
//
//        RelyingPartyRegistration registration = RelyingPartyRegistration.withRegistrationId("shibboleth-idp")
//                .entityId("https://your-app-url/saml2/service-provider-metadata")
//                .assertionConsumerServiceLocation("https://your-app-url/saml2/authenticate/shibboleth-idp")
//                .singleSignOnServiceLocation("https://your-idp-url/idp/profile/SAML2/Redirect/SSO")
//                .idpEntityId("https://your-idp-url/idp/shibboleth")
//                .credentials(c -> c.add(idpVerificationCredential))
//                .build();
//
//        return new InMemoryRelyingPartyRegistrationRepository(registration);
//    }
//
//    private X509Certificate loadCertificate(String path) throws CertificateException {
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        try (InputStream is = new ClassPathResource(path).getInputStream()) {
//            return (X509Certificate) factory.generateCertificate(is);
//        } catch (IOException e) {
//            throw new CertificateException("Could not load certificate from " + path, e);
//        }
//    }
}
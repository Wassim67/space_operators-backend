package com.spaceoperators.service;


import com.spaceoperators.payload.responses.GetMeResponseDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

	private JwtEncoder jwtEncoder;

	public JwtService(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	public String generateToken(User user) {
		Instant now = Instant.now();
		String scope = user.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(user.getUsername()) // ou ID
				.claim("email", user.getUsername()) // change si nécessaire
				.claim("scope", scope)
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.DAYS))
				.issuer("space-operators")
				.build();

		JwtEncoderParameters jwtEncoderParameters =
				JwtEncoderParameters.from(
						JwsHeader.with(MacAlgorithm.HS256).build(),
						claims);

		return this.jwtEncoder.encode(jwtEncoderParameters)
				.getTokenValue();
	}

	public GetMeResponseDTO extractUserInformation(Jwt jwt) {
		GetMeResponseDTO result = new GetMeResponseDTO();
		result.setUsername(jwt.getSubject());
		System.out.println("RESULTHAHA : " + result);
		// List<String> rolesList = new ArrayList<String>();
//		String[] roles = jwt.getClaim("scope").toString().split(" ");
//		for(int i=0; i<roles.length; i++) {
//			rolesList.add(roles[i].substring(5));
//		}
//		result.setRoles(rolesList);
		return result;
	}

}
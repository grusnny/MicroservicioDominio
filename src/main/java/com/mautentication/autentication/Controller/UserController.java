package com.mautentication.autentication.Controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.conection.FirebaseConection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import com.mautentication.autentication.dto.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class UserController {

	@PostMapping("worker")
	public User login(@RequestParam("latitude") String lat,
                      @RequestParam("length") String log,
                      @RequestParam("mail") String mail,
                      @RequestParam("mailAlt") String mailAlt,
                      @RequestParam("name") String name,
                      @RequestParam("photo") String photo,
                      @RequestParam("profession") String prof,
                      @RequestParam("telephone") String telephone,
                      @RequestParam("uId") String uId) throws IOException {

		User user = new User(mail,mailAlt,name,photo,telephone,uId);

        //Conectando al firebase del proyecto

        FirebaseConection conection=new FirebaseConection();
        try {
            System.out.println(conection.Conection());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Conectando al firebase del proyecto

        //conectando al Firestore de la instancia de firebase
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("worker").document(uId);
        //conectando al Firestore de la instancia de firebase

        //Añadiendo nuevo usuario
        Map<String, Object> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("length", log);
        data.put("mail", mail);
        data.put("mailAlt", mailAlt);
        data.put("name", name);
        data.put("photo",photo);
        data.put("telephone",telephone);
        data.put("uId",uId);
//asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
// ...
// result.get() blocks on response
        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Añadiendo nuevo usuario
//------------------esto es una prueba de datos hacia Firebase-------------------//
		return user;
	}
    @PostMapping("Exiworker")
    public int login(
                      @RequestParam("uId") String uId) throws IOException {


        //Conectando al firebase del proyecto

        FirebaseConection conection=new FirebaseConection();
        try {
            System.out.println(conection.Conection());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Conectando al firebase del proyecto

        //conectando al Firestore de la instancia de firebase
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("worker").document(uId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("capital", FieldValue.delete());
// Update and delete the "capital" field in the document
        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        try {
            System.out.println("Update time : " + writeResult.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        }

        return 1;


    }
	private String getJWTToken(String username) {

		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId("softtekJWT")
				.setSubject(username)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 600000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes()).compact();

		return "Bearer " + token;
	}
}

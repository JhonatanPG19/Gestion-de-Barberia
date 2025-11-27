package com.barberia.ms_usuarios.capaFachada.service;

import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.admin-client-id}")
    private String clientId;
    @Value("${keycloak.admin-client-secret}")
    private String clientSecret;

    public void crearUsuario(RegistroUsuarioDTO dto) {
        // 1. Conexión Admin
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        // 2. Preparar el usuario (Mapeo corregido)
        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getCorreo());
        user.setFirstName(dto.getNombre());
        user.setLastName(dto.getApellido());
        user.setEnabled(true);
        user.setEmailVerified(true);

        if(dto.getTelefono() != null)
        {
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("phoneNumber", Collections.singletonList(dto.getTelefono()));
            user.setAttributes(attributes);
        }

        // 3. Preparar la contraseña
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // 4. Crear usuario
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            // 5. Asignar el ROL (Paso Adicional Importante)
            // Necesitamos recuperar el ID (GUID) que Keycloak le asignó al usuario creado
            String userId = CreatedResponseUtil.getCreatedId(response);

            // Buscamos el rol en Keycloak (ej: "CLIENTE") que viene en el DTO
            RoleRepresentation role = realmResource.roles().get(dto.getRol()).toRepresentation();

            // Se lo asignamos al usuario
            UserResource userResource = usersResource.get(userId);
            userResource.roles().realmLevel().add(Arrays.asList(role));

            System.out.println("Usuario creado en Keycloak con ID: " + userId + " y Rol: " + dto.getRol());

        } else {
            throw new RuntimeException("Error creando usuario en Keycloak, status: " + response.getStatus());
        }
    }
}
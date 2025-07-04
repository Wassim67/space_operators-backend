package com.spaceoperators.service;

import com.spaceoperators.model.entity.EUser;
import com.spaceoperators.payload.responses.GetUserByUsernameResponseDTO;
import com.spaceoperators.payload.responses.GetUserResponseDTO;
import com.spaceoperators.repositories.EUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private PasswordEncoder passwordEncoder;
    private EUserRepository eUserRepository;
   // private ERoleRepository eRoleRepository;
   // private ECategoryRepository eCategoryRepository;

    public UserService(PasswordEncoder passwordEncoder, EUserRepository eUserRepository
                      // ERoleRepository eRoleRepository, ECategoryRepository eCategoryRepository
                        ) {
        this.passwordEncoder = passwordEncoder;
        this.eUserRepository = eUserRepository;
//        this.eRoleRepository = eRoleRepository;
//        this.eCategoryRepository = eCategoryRepository;
    }

    public List<GetUserResponseDTO> get() {
        List<EUser> entityUsers = eUserRepository.findAll();
        List<GetUserResponseDTO> dtoUsers = new ArrayList<GetUserResponseDTO>();

        entityUsers.forEach((entity) -> {
            GetUserResponseDTO dto = new GetUserResponseDTO();
            dto.setId(entity.getId());
            dto.setUsername(entity.getUsername());
//            if (entity.getCategory() != null) {
//                dto.setCategory(entity.getCategory().getName());
//            }
            List<String> roles = new ArrayList<String>();
//            entity.getRoles().forEach((role) -> {
//                roles.add(role.getName());
//            });
            dto.setRoles(roles);
            dtoUsers.add(dto);
        });
        return dtoUsers;
    }

    public GetUserByUsernameResponseDTO getByUsername(String username) {
        EUser entity = eUserRepository.findByUsername(username);

        GetUserByUsernameResponseDTO dto = new GetUserByUsernameResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());

        List<String> roles = new ArrayList<String>();
//        entity.getRoles().forEach((role) -> {
//            roles.add(role.getName());
//        });
        dto.setRoles(roles);

        return dto;
    }

//    public User validate(LoginDTO loginDto) {
//        EUser entity = eUserRepository.findByUsername(loginDto.getUsername());
//        if (entity != null && passwordEncoder.matches(loginDto.getPassword(), entity.getPassword())) {
//            return new User(entity.getUsername(), entity.getPassword(), getGrantedAuthorities(entity.getRoles()));
//        }
//        return null;
//    }

//    private List<GrantedAuthority> getGrantedAuthorities(List<ERole> roles) {
//        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        roles.forEach(role -> {
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
//        });
//        return authorities;
//    }
}

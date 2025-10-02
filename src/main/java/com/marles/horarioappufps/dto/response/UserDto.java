package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Role;
import com.marles.horarioappufps.model.User;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class UserDto {

    private String uid;
    private String email;
    private String name;
    private List<String> roles = new LinkedList<>();

    public static UserDto parseFromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setUid(user.getUid());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        //Verificar que el rol de Super Admin no se muestre
        userDto.getRoles().addAll(user
                .getRoles()
                .stream()
                .map(Role::getName)
                .filter(s -> !"ROLE_SUPERADMIN".equals(s))
                .toList());
        return userDto;
    }

    public static List<UserDto> parseFromUser(List<User> users) {
        List<UserDto> userDtos = new LinkedList<>();
        for (User user : users) {
            userDtos.add(UserDto.parseFromUser(user));
        }
        return userDtos;
    }
}

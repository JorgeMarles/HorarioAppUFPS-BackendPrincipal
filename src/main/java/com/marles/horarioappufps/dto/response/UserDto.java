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
    private List<String> roles = new LinkedList<>();

    public static UserDto parseFromDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUid(user.getUid());
        userDto.setEmail(user.getEmail());
        userDto.getRoles().addAll(user.getRoles().stream().map(Role::getName).toList());
        return userDto;
    }

    public static List<UserDto> parseFromDto(List<User> users){
        List<UserDto> userDtos = new LinkedList<>();
        for(User user : users){
            userDtos.add(UserDto.parseFromDto(user));
        }
        return userDtos;
    }
}

package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class UserDto {

    private String uid;
    private String email;

    public static UserDto parseFrom(User user){
        UserDto userDto = new UserDto();
        userDto.setUid(user.getUid());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static List<UserDto> parseFrom(List<User> users){
        List<UserDto> userDtos = new LinkedList<>();
        for(User user : users){
            userDtos.add(UserDto.parseFrom(user));
        }
        return userDtos;
    }
}

package com.fjb.sunrise.mappers;

import com.fjb.sunrise.dtos.requests.CreateAndEditUserByAdminDTO;
import com.fjb.sunrise.dtos.requests.RegisterRequest;
import com.fjb.sunrise.dtos.responses.UserResponseDTO;
import com.fjb.sunrise.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "fileCode", ignore = true)
    User toEntity(RegisterRequest request);

    @Mapping(target = "fileCode", ignore = true)
    User toEntityCreateByAdmin(CreateAndEditUserByAdminDTO byAdminDTO);

    @Mapping(target = "fileCode", ignore = true)
    UserResponseDTO toUserResponse(User user);
}

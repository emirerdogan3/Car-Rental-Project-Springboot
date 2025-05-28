package com.rentacar.mapper;

import com.rentacar.dto.CustomerDTO;
import com.rentacar.entity.Customer;
import com.rentacar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // UserMapper gibi diğer mapperları uses ile ekleyebiliriz gerekirse
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "user.userID", target = "userID")
    CustomerDTO toDto(Customer customer);

    @Mapping(source = "userID", target = "user", qualifiedByName = "mapUserIdToUser")
    Customer toEntity(CustomerDTO customerDTO);

    // Bu yardımcı metod, userID'den User entity'si oluşturur.
    // Gerçek uygulamada, bu User entity'si veritabanından çekilmeli veya
    // sadece ID'si set edilmiş bir proxy User oluşturulmalıdır.
    // Şimdilik sadece ID'yi set eden bir User oluşturalım.
    // Servis katmanında bu User'ın varlığı kontrol edilip tam entity yüklenebilir.
    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserID(userId);
        return user;
    }
} 
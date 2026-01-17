package com.sitare.service.clients;

import com.sitare.exception.UserException;
import com.sitare.grpc.*;
import com.sitare.payload.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGrpcClient {
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    public UserDTO getUserFromJwtToken(String jwt) throws UserException {
        try {
            GetUserByJwtTokenRequest request = GetUserByJwtTokenRequest.newBuilder()
                    .setJwtToken(jwt)
                    .build();
            UserResponse response = userServiceStub.getUserByJwtToken(request);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(response.getId());
            userDTO.setFullName(response.getFullName());
            userDTO.setEmail(response.getEmail());
            return userDTO;
        } catch (Exception e) {
            throw new UserException("Error fetching user from gRPC: " + e.getMessage());
        }
    }
    public UserDTO getUserById(Long userId) throws UserException {
        try {
            GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            UserResponse response = userServiceStub.getUserById(request);
            UserDTO userDTO = new UserDTO();
            userDTO.setId(response.getId());
            userDTO.setFullName(response.getFullName());
            userDTO.setEmail(response.getEmail());
            return userDTO;
        } catch (Exception e) {
            throw new UserException("Error fetching user from gRPC: " + e.getMessage());
        }
    }
}

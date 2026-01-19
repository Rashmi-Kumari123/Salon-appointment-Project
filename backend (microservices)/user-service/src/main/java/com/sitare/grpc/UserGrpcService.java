package com.sitare.grpc;
import com.sitare.exception.UserException;
import com.sitare.modal.User;
import com.sitare.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void getUserByJwtToken(GetUserByJwtTokenRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String jwtToken = request.getJwtToken();
            User user = userService.getUserFromJwtToken(jwtToken);
            
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setFullName(user.getFullName())
                    .setEmail(user.getEmail())
                    .setRole(user.getRole().name())
                    .setUsername(user.getUsername())
                    .setPhone(user.getPhone() != null ? user.getPhone() : "")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            Long userId = request.getUserId();
            User user = userService.getUserById(userId);
            
            if (user == null) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("User not found with id: " + userId)
                        .asRuntimeException());
                return;
            }
            
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setFullName(user.getFullName())
                    .setEmail(user.getEmail())
                    .setRole(user.getRole().name())
                    .setUsername(user.getUsername())
                    .setPhone(user.getPhone() != null ? user.getPhone() : "")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching user: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserByEmail(GetUserByEmailRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            String email = request.getEmail();
            User user = userService.getUserByEmail(email);
            
            UserResponse response = UserResponse.newBuilder()
                    .setId(user.getId())
                    .setFullName(user.getFullName())
                    .setEmail(user.getEmail())
                    .setRole(user.getRole().name())
                    .setUsername(user.getUsername())
                    .setPhone(user.getPhone() != null ? user.getPhone() : "")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching user: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}

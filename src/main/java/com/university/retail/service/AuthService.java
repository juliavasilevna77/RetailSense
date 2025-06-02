package com.university.retail.service;

import com.university.retail.domain.LoginDTO;
import com.university.retail.domain.RegisterDTO;
import com.university.retail.domain.UserDTO;

public interface AuthService {
    UserDTO loginUser(LoginDTO request);

    UserDTO registerUser(RegisterDTO request);
    String generateToken(UserDTO user);
}

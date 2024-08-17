package com.fathihoussam.mangaslay.Services;

import com.fathihoussam.mangaslay.Entity.User;
import com.fathihoussam.mangaslay.Repository.UserMangaInfo;
import com.fathihoussam.mangaslay.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, HttpSession session) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.session = session;

    }

    public void registerUser(String username, String email, String password) throws Exception {
        // Check if username or email already exists
        if (userRepository.findByUsername(username) != null) {
            throw new Exception("Username already exists");
        }else if (userRepository.findByEmail(email) != null) {
            throw new Exception("Email already exists");
        }

        // Create a new user object
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encode the password

        // Save the user to the database
        userRepository.save(user);
    }
    public void loginUser(String username, String password) throws Exception
    {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        else if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Wrong password");

        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());


    }


}

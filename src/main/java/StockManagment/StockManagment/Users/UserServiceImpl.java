package StockManagment.StockManagment.Users;

import StockManagment.StockManagment.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;
        private final JwtUtil jwtUtil;

        public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
            this.userRepository = userRepository;
            this.jwtUtil = jwtUtil;
        }

        @Override
        public Map<String, Object> login(String username, String password) {

            UsersEntity user = userRepository
                    .findAll()
                    .stream()
                    .filter(u -> u.getUsername().equals(username)
                            && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }

            String token = jwtUtil.generateToken(user);

            return Map.of(
                    "token", token
            );
        }
}

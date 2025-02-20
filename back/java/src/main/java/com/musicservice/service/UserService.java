import com.musicservice.model.User;
import com.musicservice.model.Role;
import com.musicservice.dto.UserDto;
import com.musicservice.repository.UserRepository;
import com.musicservice.exception.UserNotFoundException;
import com.musicservice.exception.UserAlreadyExistsException;
import com.musicservice.security.JwtTokenProvider;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public User createUser(UserDto userDto) {
        if (userRepository.existsByLogin(userDto.getLogin())) {
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .login(userDto.getLogin())
                .passwordHash(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUsername(userDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);

        return user.toDto();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getLogin(),
                            authRequest.getPasswordHash()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.createToken(
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            );
            String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

            // Обновляем время последнего входа
            User user = userRepository.findByLogin(authRequest.getLogin())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
            user.setLastLoginAt(LocalDateTime.now());
            user.setSessionToken(accessToken);
            userRepository.save(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Неверные учетные данные");
        }
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с именем " + user.getUsername() + " уже существует");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(UserRole.USER);
        
        userRepository.save(user);
    }
}
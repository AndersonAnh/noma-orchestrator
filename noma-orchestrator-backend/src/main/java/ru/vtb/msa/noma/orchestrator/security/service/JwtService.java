package ru.vtb.msa.noma.orchestrator.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.config.security.jwtproperties.JwtProperties;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;

    /**
     * Извлекает имя пользователя (subject) из JWT токена.
     * <p>
     * Метод получает значение поля "sub" (subject) из payload токена,
     * которое обычно содержит идентификатор пользователя (email, username и т.д.).
     * </p>
     *
     * <p><strong>Поток выполнения:</strong></p>
     * <ol>
     *   <li>Парсинг токена с проверкой подписи</li>
     *   <li>Извлечение claims из payload</li>
     *   <li>Получение значения subject</li>
     * </ol>
     *
     * @param token JWT токен в формате строки (без префикса "Bearer")
     * @return имя пользователя, содержащееся в токене
     * @throws io.jsonwebtoken.JwtException если токен невалиден, просрочен или имеет неверную подпись
     * @throws IllegalArgumentException     если токен имеет некорректный формат
     * @example <pre>
     * {@code
     * String token = "eyJhbGciOiJIUzI1NiJ9...";
     * String username = jwtService.extractUsername(token);
     * // Возвращает: "user@example.com"
     * }
     * </pre>
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает произвольный claim из JWT токена с использованием функции-резолвера.
     * <p>
     * Универсальный метод для извлечения любого значения из payload токена.
     * Использует {@link Function} для преобразования claims в нужный тип данных.
     * </p>
     *
     * <p><strong>Поддерживаемые claim-ы:</strong></p>
     * <ul>
     *   <li>Стандартные (registered): sub, exp, iat, iss, aud и др.</li>
     *   <li>Публичные (public): email, role, userId и др.</li>
     *   <li>Приватные (private): любые пользовательские данные</li>
     * </ul>
     *
     * @param <T>            тип возвращаемого значения
     * @param token          JWT токен в формате строки
     * @param claimsResolver функция для извлечения конкретного claim из объекта {@link Claims}
     * @return значение claim указанного типа
     * @throws io.jsonwebtoken.JwtException если токен невалиден
     * @example <pre>
     * {@code
     * // Извлечение expiration time
     * Date exp = jwtService.extractClaim(token, Claims::getExpiration);
     *
     * // Извлечение кастомного claim
     * String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
     *
     * // Извлечение userId как Long
     * Long userId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));
     * }
     * </pre>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Генерирует JWT токен для указанного пользователя.
     * <p>
     * Создает токен с claims по умолчанию, содержащими только subject (имя пользователя).
     * Время жизни токена определяется константой {@link #jwtProperties}.
     * </p>
     *
     * <p><strong>Структура генерируемого токена:</strong></p>
     * <ul>
     *   <li><strong>sub:</strong> имя пользователя (username из UserDetails)</li>
     *   <li><strong>iat:</strong> время создания токена (issued at)</li>
     *   <li><strong>exp:</strong> время истечения токена (issued at + {@link #jwtProperties})</li>
     *   <li><strong>alg:</strong> HS256 (HMAC SHA256)</li>
     * </ul>
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о пользователе
     * @return сгенерированный JWT токен в формате строки
     * @throws IllegalArgumentException если userDetails или username равны null
     * @example <pre>
     * {@code
     * UserDetails user = userDetailsService.loadUserByUsername("user@example.com");
     * String token = jwtService.generateToken(user);
     * // Возвращает: "eyJhbGciOiJIUzI1NiJ9..."
     * }
     * </pre>
     * @see #generateToken(Map, UserDetails)
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Генерирует JWT токен с дополнительными claims для указанного пользователя.
     * <p>
     * Позволяет добавить произвольные данные (claims) в payload токена.
     * Переданные claims объединяются со стандартными claims (sub, iat, exp).
     * </p>
     *
     * <p><strong>Важно:</strong> Не добавляйте чувствительные данные в claims,
     * так как payload JWT токена может быть легко декодирован.</p>
     *
     * <p><strong>Пример дополнительных claims:</strong></p>
     * <ul>
     *   <li>Роли пользователя: "roles": ["ROLE_ADMIN", "ROLE_USER"]</li>
     *   <li>Идентификатор пользователя: "userId": 12345</li>
     *   <li>Email: "email": "user@example.com"</li>
     *   <li>Дополнительные метаданные</li>
     * </ul>
     *
     * @param extractClaims дополнительные claims, которые будут добавлены в payload токена
     * @param userDetails   объект {@link UserDetails}, содержащий информацию о пользователе
     * @return сгенерированный JWT токен в формате строки
     * @throws IllegalArgumentException если userDetails, username или claims равны null
     * @example <pre>
     * {@code
     * Map<String, Object> claims = new HashMap<>();
     * claims.put("userId", 12345);
     * claims.put("roles", Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
     *
     * UserDetails user = userDetailsService.loadUserByUsername("user@example.com");
     * String token = jwtService.generateToken(claims, user);
     * }
     * </pre>
     */
    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSigInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет валидность JWT токена для указанного пользователя.
     * <p>
     * Выполняет две основные проверки:
     * </p>
     * <ol>
     *   <li>Имя пользователя из токена соответствует имени пользователя из UserDetails</li>
     *   <li>Срок действия токена не истек</li>
     * </ol>
     *
     * <p><strong>Дополнительные проверки, выполняемые автоматически при парсинге:</strong></p>
     * <ul>
     *   <li>Корректность подписи токена (signature verification)</li>
     *   <li>Правильность формата токена (JWT structure)</li>
     *   <li>Совпадение алгоритма подписи с ожидаемым (HS256)</li>
     * </ul>
     *
     * @param token       JWT токен для проверки
     * @param userDetails объект {@link UserDetails} для сравнения данных
     * @return true если токен валиден для пользователя, false в противном случае
     * @throws io.jsonwebtoken.JwtException если токен имеет некорректный формат или подпись
     * @example <pre>
     * {@code
     * String token = "eyJhbGciOiJIUzI1NiJ9...";
     * UserDetails user = userDetailsService.loadUserByUsername("user@example.com");
     * boolean isValid = jwtService.isTokenValid(token, user);
     * // Возвращает: true (если токен валиден) или false
     * }
     * </pre>
     * @see #extractUsername(String)
     * @see #isTokenExpired(String)
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Извлекает authorities (роли) из JWT токена.
     * <p>
     * Предполагает, что authorities хранятся в claim с ключом "authorities"
     * в формате списка строк.
     * </p>
     *
     * @param token JWT токен
     * @return список authorities или пустой список, если claim отсутствует
     */
    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        Claims claims = extractClaims(token);
        List<String> authorities = (List<String>) claims.get("authorities");
        return authorities != null ? authorities : Collections.emptyList();
    }

    /**
     * Генерирует токен с authorities из UserDetails.
     *
     * @param userDetails объект UserDetails
     * @return JWT токен с authorities
     */
    public String generateTokenWithAuthorities(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("authorities", authorities);
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет, истек ли срок действия JWT токена.
     * <p>
     * Сравнивает дату истечения токена (claim "exp") с текущим временем.
     * Если claim "exp" отсутствует в токене, считается что токен бессрочный
     * (метод вернет false).
     * </p>
     *
     * @param token JWT токен для проверки
     * @return true если срок действия токена истек, false если токен еще действителен
     * @throws io.jsonwebtoken.JwtException если токен имеет некорректный формат
     * @see #extractExpiration(String)
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия JWT токена.
     * <p>
     * Получает значение claim "exp" (expiration time) из payload токена.
     * </p>
     *
     * @param token JWT токен
     * @return дата истечения срока действия токена
     * @throws io.jsonwebtoken.JwtException если токен невалиден или не содержит claim "exp"
     * @example <pre>
     * {@code
     * String token = "eyJhbGciOiJIUzI1NiJ9...";
     * Date expiration = jwtService.extractExpiration(token);
     * // Возвращает: Sat Mar 16 12:30:45 UTC 2024
     * }
     * </pre>
     * @see Claims#getExpiration()
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все claims из JWT токена.
     * <p>
     * Приватный метод, который выполняет:
     * </p>
     * <ol>
     *   <li>Парсинг токена с использованием секретного ключа</li>
     *   <li>Верификацию подписи токена</li>
     *   <li>Проверку expiration времени (если есть claim 'exp')</li>
     *   <li>Возврат всех claims из payload</li>
     * </ol>
     *
     * <p><strong>Валидации, выполняемые автоматически:</strong></p>
     * <ul>
     *   <li>Подпись токена (signature verification)</li>
     *   <li>Expiration время (если claim 'exp' присутствует)</li>
     *   <li>NotBefore время (если claim 'nbf' присутствует)</li>
     * </ul>
     *
     * @param token JWT токен в формате строки
     * @return объект {@link Claims}, содержащий все claims из payload токена
     * @throws io.jsonwebtoken.ExpiredJwtException        если токен просрочен
     * @throws io.jsonwebtoken.SignatureException         если подпись токена неверна
     * @throws io.jsonwebtoken.MalformedJwtException      если токен имеет некорректный формат
     * @throws io.jsonwebtoken.UnsupportedJwtException    если токен использует неподдерживаемый алгоритм
     * @throws io.jsonwebtoken.security.SecurityException при проблемах с безопасностью
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Создает и возвращает ключ для верификации подписи JWT токенов.
     * <p>
     * Метод декодирует Base64-строку секретного ключа и создает объект {@link Key}
     * для алгоритма HMAC SHA256.
     * </p>
     *
     * <p><strong>Технические детали:</strong></p>
     * <ul>
     *   <li>Использует {@link Decoders#BASE64} для декодирования строки</li>
     *   <li>Создает HMAC SHA256 ключ через {@link Keys#hmacShaKeyFor(byte[])}</li>
     *   <li>Ключ кэшируется внутри JWT парсера для производительности</li>
     * </ul>
     *
     * @return объект {@link Key} для верификации подписи токенов
     * @throws IllegalArgumentException                  если SECRET_KEY имеет некорректный формат Base64
     * @throws io.jsonwebtoken.security.WeakKeyException если ключ недостаточно длинный
     * @see Decoders#BASE64
     * @see Keys#hmacShaKeyFor(byte[])
     */
    private SecretKey getSigInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

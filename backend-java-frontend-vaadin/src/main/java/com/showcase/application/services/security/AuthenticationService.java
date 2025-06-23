package com.showcase.application.services.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.models.configuration.Parameter;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.rest.dto.UserDto;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.repositories.security.TokenRepository;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.Utilities;
import com.showcase.application.utils.exceptions.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {

    private final AppInfo appInfo;
    private final TokenRepository tokenRepository;
    private final ParameterService parameterService;
    private final UserSettingService userSettingService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Token findByTokenAndEnabled(String tokenHash, boolean enabled) {
        Token token = tokenRepository.findByTokenAndEnabledAndUser_EnabledIsTrue(tokenHash, enabled);

        if (token == null) {
            throw new MyException(MyException.NO_TOKEN_FOUND, "The token doesnt exist");
        }

        UserSetting userSetting = userSettingService.findByEnabledAndUser(true, token.getUser());

        if (userSetting == null) {
            userSetting = new UserSetting();
        }

        if (token.getExpirationDate().toInstant().isBefore(new Date().toInstant().atZone(userSetting.getTimezone().toZoneId()).toInstant())) {
            throw new MyException(MyException.NO_TOKEN_FOUND, "The token has expired");
        }

        return token;
    }

    @Transactional(readOnly = true)
    public Token findByUserAndEnabled(User user, boolean enabled) {
        return tokenRepository.findFirstByUserAndEnabled(user, enabled);
    }

    public Token generateToken(User user) {
        try {

            if (user == null) {
                throw new MyException(MyException.CLIENT_ERROR, "User not found");
            }

            UserSetting userSetting = userSettingService.findByEnabledAndUser(true, user);
            TimeZone timeZone = userSetting != null ? userSetting.getTimezone() : TimeZone.getTimeZone(GlobalConstants.DEFAULT_TIMEZONE);

            Token token = findByUserAndEnabled(user, true);

            if (token == null) {
                token = new Token();
                token.setUser(user);

                Calendar cal = Calendar.getInstance(timeZone);
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                try {
                    token.setExpirationDate(DateUtils.addDays(cal.getTime(), Integer.parseInt(String.valueOf(parameterService.findFirstByCode(Parameter.TOKEN_EXPIRATION_DAYS).getValue()))));
                } catch (NumberFormatException ignored) {
                    token.setExpirationDate(DateUtils.addDays(cal.getTime(), 7));
                }
                token.setToken(Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(user.getUsername().getBytes())));
                token = tokenRepository.saveAndFlush(token);
            }

            if (token.getExpirationDate().toInstant().isBefore(new Date().toInstant().atZone(timeZone.toZoneId()).toInstant())) {
                token.setEnabled(false);
                tokenRepository.saveAndFlush(token);

                token = new Token();
                token.setUser(user);

                Calendar cal = Calendar.getInstance(timeZone);
                cal.setTime(new Date());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                try {
                    token.setExpirationDate(DateUtils.addDays(cal.getTime(), Integer.parseInt(String.valueOf(parameterService.findFirstByCode(Parameter.TOKEN_EXPIRATION_DAYS).getValue()))));
                } catch (NumberFormatException ignored) {
                    token.setExpirationDate(DateUtils.addDays(cal.getTime(), 7));
                }

                token.setToken(Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest((user.getUsername() + "/" + UUID.randomUUID()).getBytes())));
                token = tokenRepository.saveAndFlush(token);
            }

            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new MyException(MyException.SERVER_ERROR, "Error generating Token:\n\n" + e.getMessage());
        }
    }

    public Claims isJWTValid(String token) {
        try {
            token = token.replace("Bearer", "").trim();

            Claims claims = Jwts.parser()
                    .verifyWith(Utilities.generateJWTKey(appInfo.getJwtSecretKey()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

//            System.out.println("Expires in:" + Utilities.formatDate(claims.getExpiration(),"", ""));

            return claims;
        } catch (Exception ignored) {
            return null;
        }
    }

    public String getJWTPayload(String token) {
        try {
            token = token.replace("Bearer", "").trim();

            Claims claims = Jwts.parser()
                    .verifyWith(Utilities.generateJWTKey(appInfo.getJwtSecretKey()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

//            return Utilities.decrypt(claims.get("token").toString(), Utilities.generateSecretKey());
            return claims.get("token").toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    public String generateJWT(Token token) {
        try {

            if (token == null) {
                throw new MyException(MyException.SERVER_ERROR, "Server token is null");
            }

            UserSetting userSetting = userSettingService.findByEnabledAndUser(true, token.getUser());
            if (userSetting == null) {
                userSetting = new UserSetting();
            }
            if (token.getExpirationDate().toInstant().isBefore(new Date().toInstant().atZone(userSetting.getTimezone().toZoneId()).toInstant())) {
                token = generateToken(token.getUser());
            }

//            String encryptedData = Utilities.encrypt(token.getToken(), Utilities.generateSecretKey());
//            String decrypted = Utilities.decrypt(token.getToken(), Utilities.generateSecretKey());

//            System.out.println("token: " + token.getToken());
//            System.out.println("encripted: " + encryptedData);
//            System.out.println("decrypted: " + decrypted);

            return Jwts.builder()
                    .issuer("Framework-Showcase-App")
                    .subject("data")
                    .claim("token", token.getToken())
                    .signWith(Utilities.generateJWTKey(appInfo.getJwtSecretKey()))
                    .expiration(Date.from(token.getExpirationDate().toInstant().atZone(userSetting.getTimezone().toZoneId()).toInstant()))
                    .compact();

        } catch (MyException ignored) {
            return "";
        }
    }

    @Transactional(readOnly = true)
    public User login(UserDto userDto) {
        try {
            User user = userService.findByUsernameOrMail(userDto.getUsername());

//            System.out.println("Compare:");
//            System.out.println(userDao.getPassword());
//            System.out.println(user.getPassword());

            if (!userService.getPasswordEncoder().matches(userDto.getPassword(), user.getPassword())) {
                throw new MyException(MyException.CLIENT_ERROR, "Invalid credentials");
            }

            return user;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }
}

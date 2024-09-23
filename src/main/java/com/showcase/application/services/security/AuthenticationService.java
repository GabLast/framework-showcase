package com.showcase.application.services.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.models.configuration.Parameter;
import com.showcase.application.models.configuration.UserSetting;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.repositories.security.TokenRepository;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.utils.GlobalConstants;
import com.showcase.application.utils.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AppInfo appInfo;
    private final TokenRepository tokenRepository;
    private final ParameterService parameterService;
    private final UserSettingService userSettingService;

    @Transactional(readOnly = true)
    public Token findByTokenAndEnabled(String hash, boolean enabled) {
        Token token = tokenRepository.findByTokenAndEnabledAndUser_EnabledIsTrue(hash, enabled);

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

    private void isJWTValid(String token) {

        try {
            token = token.replace("Bearer", "").trim();
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(appInfo.getJwtSecretKey().getBytes()))
                    .build()
                    .parseSignedClaims("receivedJWT")
                    .getPayload();
        } catch (Exception ignored) {

        }
    }


//
//    FilterTestData fromJason = new Gson().fromJson(claims.get("data").toString(), FilterTestData.class);
//
//    String jwt = Jwts.builder()
//            .issuer("Framework-Showcase-App")
//            .subject("data")
//            .claim("data", filterTestData)
//            .signWith(Utilities.generateJWTKey(parameterService.findFirstByEnabledAndCode(true, Parameter.JWT_KEY).getValue()))
//            .compact();
}

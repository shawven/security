package com.github.shawven.security.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.shawven.security.authorization.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

/**
 * @author Shoven
 * @date 2018/11/5 18:01
 */
public class OAuthExceptionJacksonSerializer extends StdSerializer<OAuth2Exception> {

    protected OAuthExceptionJacksonSerializer() {
        super(OAuth2Exception.class);
    }

    @Override
    public void serialize(OAuth2Exception value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        String errorMessage = value.getOAuth2ErrorCode();
        if (errorMessage != null) {
            errorMessage = HtmlUtils.htmlEscape(errorMessage);
        }

        ResponseData response = new ResponseData()
                .setMessage(errorMessage + ": " + value.getMessage())
                .setCode(HttpStatus.UNAUTHORIZED.value());

        jgen.writeObject(response);
    }
}

package cc.coodex.concrete.attachments.client;

import cc.coodex.concrete.common.*;
import cc.coodex.concrete.core.token.TokenManager;
import cc.coodex.concrete.core.token.TokenWrapper;
import cc.coodex.util.Common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static cc.coodex.concrete.attachments.AttachmentServiceHelper.ATTACHMENT_PROFILE;

/**
 * Created by davidoff shen on 2016-12-13.
 */
public class ClientServiceImpl implements ClientService {


    private static final TokenManager tokenManager = BeanProviderFacade.getBeanProvider().getBean(TokenManager.class);

    private static final Token token = TokenWrapper.getInstance();

    public static final String ATTACHMENT_AUTHORIZATION_KEY = ClientServiceImpl.class.getName() + ".AUTHORIZATIONS";
    public static final String ATTACHMENT_WRITABLE_KEY = ClientServiceImpl.class.getName() + ".WRITABLE";


    public static void allowWrite() {
        if (token.getAttribute(ATTACHMENT_WRITABLE_KEY) == null)
            token.setAttribute(ATTACHMENT_WRITABLE_KEY, "OK");
    }


    public static void allow(Set<String> attachmentIds) {
        allow(token, attachmentIds);
    }

    private static void allow(Token token, Set<String> attachmentIds) {
        HashMap<String, Long> attachments;
        synchronized (ClientServiceImpl.class) {
            attachments = token.getAttribute(ATTACHMENT_AUTHORIZATION_KEY);
            if (attachments == null) {
                attachments = new HashMap<String, Long>();
                token.setAttribute(ATTACHMENT_AUTHORIZATION_KEY, attachments);
            }
        }

        long validity = System.currentTimeMillis() + ATTACHMENT_PROFILE.getLong("attachment.validity", 10) * 1000 * 60;
        for (String attachmentId : attachmentIds) {
            if (attachmentId != null) {
                attachments.put(attachmentId, validity);
            }
        }
        token.flush();
    }

    public static void allow(String... attachmentIds) {
        allow((Set<String>) Common.arrayToSet(attachmentIds));
    }


    private Token getTokenById(String tokenId) {
        Token token = tokenManager.getToken(tokenId);
        Assert.isNull(token, ErrorCodes.NONE_TOKEN);
        Assert.not(token.isValid(), ErrorCodes.TOKEN_INVALIDATE, tokenId);
        return token;
    }


    private boolean isAuthorized(String tokenId, String attachmentId) {
        Token token = getTokenById(tokenId);
        HashMap<String, Long> authorizations = token.getAttribute(ATTACHMENT_AUTHORIZATION_KEY);
        Long validity = authorizations.get(attachmentId);
        if (validity == null || validity.longValue() < System.currentTimeMillis()) {
            authorizations.remove(attachmentId);
            token.flush();
            return false;
        } else
            return true;
    }


    @Override
    public boolean readable(String token, String attachmentId) {
        return isAuthorized(token, attachmentId);
    }

    @Override
    public boolean writable(String token) {
        return getTokenById(token).getAttribute(ATTACHMENT_WRITABLE_KEY) != null;
    }


    @Override
    public boolean deletable(String token, String attachmentId) {
        return writable(token) && readable(token, attachmentId);
    }

    @Override
    public void notify(String token, String attachmentId) {
        Token t = tokenManager.getToken(token);
        if (t != null) {
            Set<String> set = new HashSet<String>();
            set.add(attachmentId);
            allow(t, set);
        }
    }
}

/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.delivery.webserver.pages;

import com.djrapitops.plan.delivery.domain.WebUser_old;
import com.djrapitops.plan.delivery.web.resolver.*;
import com.djrapitops.plan.delivery.webserver.Request;
import com.djrapitops.plan.delivery.webserver.RequestTarget;
import com.djrapitops.plan.delivery.webserver.auth.Authentication;
import com.djrapitops.plan.delivery.webserver.response.ResponseFactory;
import com.djrapitops.plan.delivery.webserver.response.Response_old;
import com.djrapitops.plan.exceptions.WebUserAuthException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Resolves /debug URL.
 *
 * @author Rsl1122
 */
@Singleton
public class DebugPageResolver implements PageResolver, Resolver {

    private final ResponseFactory responseFactory;

    @Inject
    public DebugPageResolver(ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    @Override
    public boolean canAccess(WebUser permissions, URIPath target, URIQuery query) {
        return permissions.hasPermission("page.debug");
    }

    @Override
    public Optional<Response> resolve(URIPath target, URIQuery query) {
        return Optional.of(responseFactory.debugPageResponse());
    }

    @Override
    public Response_old resolve(Request request, RequestTarget target) {
        return responseFactory.debugPageResponse_old();
    }

    @Override
    public boolean isAuthorized(Authentication auth, RequestTarget target) throws WebUserAuthException {
        WebUser_old webUser = auth.getWebUser();
        return webUser.getPermLevel() <= 0;
    }
}

/*
 * Copyright (C) 2005 Les Hazlewood
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 *
 * Free Software Foundation, Inc.
 * 59 Temple Place, Suite 330
 * Boston, MA 02111-1307
 * USA
 *
 * Or, you may view it online at
 * http://www.opensource.org/licenses/lgpl-license.php
 */
package org.jsecurity.spring.servlet.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsecurity.ri.web.WebSessionFactory;
import org.jsecurity.ri.web.WebUtils;
import org.jsecurity.session.InvalidSessionException;
import org.jsecurity.session.Session;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ensures a JSecurity {@link Session Session} exists for an incoming {@link HttpServletRequest}.
 *
 * <p>If an existing <tt>Session</tt> can be found that is already associated with the client
 * executing the <tt>HttpServletRequest</tt>, it will be retrieved and made accessible.
 *
 * <p>If no existing <tt>Session</tt> could be associated with the <tt>HttpServletRequest</tt>,
 * this interceptor will create a new one, associate it with the <tt>request</tt>'s corresponding
 * client, and be made accessible to the JSecurity framework for the duration of the
 * request (i.e. via a {@link ThreadLocal ThreadLocal}).
 *
 * @since 0.1
 * @author Les Hazlewood
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    protected transient final Log log = LogFactory.getLog( getClass() );

    private WebSessionFactory webSessionFactory = null;

    public void setWebSessionFactory( WebSessionFactory webSessionFactory ) {
        this.webSessionFactory = webSessionFactory;
    }

    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                              Object handler ) throws Exception {

        boolean continueProcessing = true;

        try {
            Session session = webSessionFactory.getSession( request, response );
            if ( session == null ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( "No JSecurity Session associated with the HttpServletRequest.  " +
                               "Attempting to create a new one." );
                }
                session = webSessionFactory.start( request, response );
                if ( log.isDebugEnabled() ) {
                    log.debug( "Created new JSecurity Session with id [" +
                               session.getSessionId() + "]");
                }
            } else {
                //update last accessed time:
                session.touch();
            }

            WebUtils.bindToThread( session );

        } catch ( InvalidSessionException ise ) {
            if ( log.isTraceEnabled() ) {
                log.trace( "Request JSecurity Session is invalid, message: [" +
                           ise.getMessage() + "].");
            }
            continueProcessing = handleInvalidSession( request, response, handler, ise );
        }

        return continueProcessing;
    }

    public void afterCompletion( HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex ) throws Exception {
        WebUtils.unbindSessionFromThread();
    }

    protected boolean handleInvalidSession( HttpServletRequest request,
                                            HttpServletResponse response,
                                            Object handler,
                                            InvalidSessionException ise ) {
        if ( log.isTraceEnabled() ) {
            log.trace( "Handling invalid session associated with the request.  Attempting to " +
                       "create a new Session to allow processing to continue" );
        }
        Session s = webSessionFactory.start( request, response );
        WebUtils.bindToThread( s );

        if ( log.isTraceEnabled() ) {
            log.trace( "Adding EXPIRED_SESSION_KEY as a request attribute to alert that the request's incoming " +
                "referenced session has expired." );
        }
        request.setAttribute( WebUtils.EXPIRED_SESSION_KEY, Boolean.TRUE );

        return true;
    }



}

/*
* Copyright (C) 2005-2007 Jeremy Haile
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

/**
 * Represents the method used to store the page that the user was attempting
 * to load, when redirecting to the login page by the
 * {@link AuthenticationInterceptor} (e.g. storing the attempted page in the
 * session)
 *
 * @since 0.2
 * @author Jeremy Haile
 */
public enum AttemptedPageStorageScheme {
    requestParameter,
    httpSession,
    jsecuritySession
}

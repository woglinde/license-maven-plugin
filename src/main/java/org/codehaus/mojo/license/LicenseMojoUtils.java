package org.codehaus.mojo.license;

/*
 * #%L
 * License Maven Plugin
 * %%
 * Copyright (C) 2018 Codehaus
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.mojo.license.utils.UrlRequester;

/**
 * Utility methods common to various mojos.
 *
 * @since 1.17
 */
public final class LicenseMojoUtils
{
    /** A special singleton to pass the information that the URL was not set. */
    static final String NO_URL = "file:///inexistent";
    static final String DEFAULT_OVERRIDE_THIRD_PARTY = "src/license/override-THIRD-PARTY.properties";

    /**
     * Hidden
     */
    private LicenseMojoUtils()
    {
    }

    /**
     * @param url the {@link URL} to check
     * @return {@code url != null && url != NO_URL}
     */
    public static boolean isValid( String url )
    {
        return url != null && !NO_URL.equals( url );
    }

    /**
     * Chooses the override {@link URL} to use out of {@code resolvedUrl}, {@code deprecatedFile}, {@code url}, or the
     * default given by {@link #DEFAULT_OVERRIDE_THIRD_PARTY}.
     *
     * @param resolvedUrl returns this one if it is not {@code null} and not equal to {@link #NO_URL}
     * @param deprecatedFile the deprecated mojo parameter
     * @param url the newer variant of the mojo parameter
     * @param basedir {@code basedir} to resolve {@value #DEFAULT_OVERRIDE_THIRD_PARTY} against
     * @return a valid URL or {@link #NO_URL}, never {@code null}
     */
    static String prepareThirdPartyOverrideUrl( final String resolvedUrl, final File deprecatedFile, final String url,
            File basedir, Log log )
    {
        if ( deprecatedFile != null )
        {
            log.warn( "'overrideFile' mojo parameter is deprecated. Use 'overrideUrl' instead." );
        }
        return prepareUrl( resolvedUrl, deprecatedFile, url, basedir, DEFAULT_OVERRIDE_THIRD_PARTY );
    }

    private static String prepareUrl( final String resolvedUrl, final File deprecatedFile, final String url,
            File basedir, String defaultFilePath )
    {
        if ( resolvedUrl != null && !NO_URL.equals( resolvedUrl ) )
        {
            return resolvedUrl;
        }

        if ( deprecatedFile != null && url != null && !url.isEmpty() )
        {
            throw new IllegalArgumentException( "You can't use both overrideFile and overrideUrl" );
        }

        if ( deprecatedFile != null && deprecatedFile.exists() )
        {
            return deprecatedFile.toURI().toString();
        }

        final Path basedirPath = basedir.toPath();

        if ( url != null && UrlRequester.isStringUrl( url ) )
        {
            return basedirPath.toUri().toString();
        }

        final Path defaultPath = basedirPath.resolve( defaultFilePath );

        if ( Files.exists( defaultPath ) )
        {
            return defaultPath.toUri().toString();
        }

        return NO_URL;
    }

}

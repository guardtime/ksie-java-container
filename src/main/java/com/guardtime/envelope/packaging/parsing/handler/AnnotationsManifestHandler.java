/*
 * Copyright 2013-2018 Guardtime, Inc.
 *
 * This file is part of the Guardtime client SDK.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES, CONDITIONS, OR OTHER LICENSES OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * "Guardtime" and "KSI" are trademarks or registered trademarks of
 * Guardtime, Inc., and no license to trademarks is granted; Guardtime
 * reserves and retains all trademark rights.
 */

package com.guardtime.envelope.packaging.parsing.handler;

import com.guardtime.envelope.manifest.AnnotationsManifest;
import com.guardtime.envelope.manifest.EnvelopeManifestFactory;
import com.guardtime.envelope.manifest.InvalidManifestException;

import java.io.InputStream;

/**
 * This content holders is used for annotations manifests inside the envelope.
 */
public class AnnotationsManifestHandler implements ContentHandler<AnnotationsManifest> {

    private final EnvelopeManifestFactory manifestFactory;

    public AnnotationsManifestHandler(EnvelopeManifestFactory manifestFactory) {
        this.manifestFactory = manifestFactory;
    }

    @Override
    public AnnotationsManifest parse(InputStream stream, String path) throws ContentParsingException {
        try {
            return manifestFactory.readAnnotationsManifest(stream, path);
        } catch (InvalidManifestException e) {
            throw new ContentParsingException("Failed to parse content of stream as AnnotationsManifest.", e);
        }
    }

}

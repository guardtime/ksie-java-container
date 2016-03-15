package com.guardtime.container.packaging.zip.handler;

import com.guardtime.container.manifest.ContainerManifestFactory;
import com.guardtime.container.manifest.InvalidManifestException;
import com.guardtime.container.manifest.SignatureManifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ManifestHolder extends ContentHandler<SignatureManifest> {

    private final ContainerManifestFactory manifestFactory;

    public ManifestHolder(ContainerManifestFactory manifestFactory) {
        this.manifestFactory = manifestFactory;
    }

    @Override
    public boolean isSupported(String name) {
        return matchesSingleDirectory(name, "META-INF") &&
                fileNameMatches(name, "manifest[0-9]+." + manifestFactory.getManifestFactoryType().getManifestFileExtension());
    }

    @Override
    public SignatureManifest get(String name) throws ContentParsingException {
        File file = entries.get(name);
        try (FileInputStream input = new FileInputStream(file)) {
            return manifestFactory.readSignatureManifest(input);
        } catch (InvalidManifestException e) {
            throw new ContentParsingException("Failed to parse content of manifest file", e);
        } catch (FileNotFoundException e) {
            throw new ContentParsingException("Failed to locate requested file in filesystem", e);
        } catch (IOException e) {
            throw new ContentParsingException("Failed to read file", e);
        }
    }

}

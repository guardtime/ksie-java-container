package com.guardtime.envelope.signature.postponed;

import com.guardtime.envelope.signature.EnvelopeSignature;
import com.guardtime.ksi.hashing.DataHash;

import java.io.IOException;
import java.io.OutputStream;

// TODO: Javadoc
class PostponedSignature implements EnvelopeSignature {

    private final DataHash dataHash;
    private EnvelopeSignature internalSignature = null;

    public PostponedSignature(DataHash hash) {
        this.dataHash = hash;
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        if(internalSignature == null) {
            output.write(dataHash.getImprint());
        } else {
            internalSignature.writeTo(output);
        }
    }

    @Override
    public Object getSignature() {
        if(internalSignature != null) {
            return internalSignature.getSignature();
        }
        return null;
    }

    @Override
    public DataHash getSignedDataHash() {
        if(internalSignature == null) {
            return dataHash;
        }
        return internalSignature.getSignedDataHash();
    }

    @Override
    public boolean isExtended() {
        if(internalSignature == null) {
            return false;
        }
        return internalSignature.isExtended();
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    void sign(EnvelopeSignature realSignature) {
        if(internalSignature != null) {
            throw new UnsupportedOperationException();
        }
        this.internalSignature = realSignature;
    }

}

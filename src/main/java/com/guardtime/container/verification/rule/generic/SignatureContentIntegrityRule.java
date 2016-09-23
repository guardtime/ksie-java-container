package com.guardtime.container.verification.rule.generic;

import com.guardtime.container.packaging.Container;
import com.guardtime.container.packaging.SignatureContent;
import com.guardtime.container.verification.result.ResultHolder;
import com.guardtime.container.verification.rule.*;
import com.guardtime.container.verification.rule.signature.SignatureVerifier;

/**
 * This is a delegating rule, not verifying directly but by calling relevant rules to verify sub-components. This rule
 * verifies each {@link SignatureContent} in a {@link Container} by calling rules to verify the signature, the documents
 * and the annotations contained by the {@link SignatureContent}.
 */
public class SignatureContentIntegrityRule extends AbstractRule<Container> implements ContainerRule {
    private final ContainerSignatureIntegrityRule signatureIntegrityRule;
    private DocumentsIntegrityRule documentsIntegrityRule;
    private AnnotationsIntegrityRule annotationsIntegrityRule;
    private SignatureExistenceRule signatureExistenceRule;

    public SignatureContentIntegrityRule(RuleStateProvider stateProvider, SignatureVerifier signatureVerifier) {
        super(RuleState.FAIL);
        this.signatureExistenceRule = new SignatureExistenceRule(stateProvider);
        this.signatureIntegrityRule = new ContainerSignatureIntegrityRule(stateProvider, signatureVerifier);
        this.documentsIntegrityRule = new DocumentsIntegrityRule(stateProvider);
        this.annotationsIntegrityRule = new AnnotationsIntegrityRule(stateProvider);
    }

    @Override
    protected void verifyRule(ResultHolder holder, Container verifiable) {
        for (SignatureContent content : verifiable.getSignatureContents()) {
            try {
                signatureExistenceRule.verify(holder, content);
                signatureIntegrityRule.verify(holder, content);
                documentsIntegrityRule.verify(holder, content);
                annotationsIntegrityRule.verify(holder, content);
            } catch (RuleTerminatingException e) {
                LOGGER.info("Halting signature verification chain! Caused by '{}'", e.getMessage());
            }
        }
    }

}

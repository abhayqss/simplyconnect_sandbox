package org.openhealthtools.common.utils;

import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Reason for creating this class is reimplementation <code>AssigningAuthorityUtil.reconcileIdentifier</code> method of . Here we save namespaceId
 * for reconciled identifier if it was not specified in connection configuration xml for organisation. Currently, the reason
 * for not specifying namespaceId for QUALIFACTS is that they send different namespaces in format 'QUAL-{{organisation}}'
 *
 */
public final class CustomAssigningAuthorityUtil {

    private CustomAssigningAuthorityUtil() {}

    public static Identifier reconcileIdentifier(Identifier authority, IConnectionDescription connection) {
        // [copypaste]----- Pasted from original AssigningAuthorityUtil.reconcileIdentifier --------------
        final List<Identifier> identifiers = connection.getAllIdentifiersByType("domain");
        final Iterator i$ = identifiers.iterator();

        Identifier identifier;
        do {
            if (!i$.hasNext()) {
                return authority;
            }

            identifier = (Identifier)i$.next();
        } while(!identifier.equals(authority));
        // -----------------------------------------------------------------------------------------------

        if (StringUtils.isEmpty(identifier.getNamespaceId())) {
            identifier.setNamespaceId(authority.getNamespaceId());
        }

        return identifier;
    }
}

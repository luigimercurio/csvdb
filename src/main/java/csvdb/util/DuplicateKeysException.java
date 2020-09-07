package csvdb.util;

import csvdb.model.Invoice;

import java.util.Set;

public class DuplicateKeysException extends RuntimeException {
    Set<Invoice.Key> duplicateKeys;

    public DuplicateKeysException(Set<Invoice.Key> duplicates) {
        super("Duplicate pairs 'Supplier Id' / 'Invoice Id' in uploaded file");
        duplicateKeys = duplicates;
    }

    public Set<Invoice.Key> getDuplicateKeys() {
        return duplicateKeys;
    }
}

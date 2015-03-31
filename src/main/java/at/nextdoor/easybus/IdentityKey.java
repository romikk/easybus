package at.nextdoor.easybus;

import java.lang.ref.WeakReference;

/**
 * Created by romikk on 31/03/15.
 */
public class IdentityKey {
    private final WeakReference ref;
    private final int hashCode;

    @SuppressWarnings("unchecked")
    public IdentityKey(final Object obj) {
        this.ref = new WeakReference(obj);
        this.hashCode = obj.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        IdentityKey other = (IdentityKey) o;
        return this.ref.get() == other.ref.get();
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}

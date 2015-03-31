package at.nextdoor.easybus;

/**
 * Created by romikk on 31/03/15.
 */
public class IdentityKey {
    private final Object obj;

    public IdentityKey(final Object obj) {
        this.obj = obj;
    }

    @Override
    public boolean equals(final Object o) {
        return this.obj == ((IdentityKey)o).obj;
    }

    @Override
    public int hashCode() {
        return obj.hashCode();
    }
}

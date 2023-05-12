package learnyouakotlin.part4;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemorySessionSignups implements SessionSignups {
    private final Map<SessionId, SessionSignup> signupsById = new HashMap<>();
    private final Lock readLock;
    private final Lock writeLock;

    {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    public @Nullable SessionSignup load(SessionId id) {
        readLock.lock();
        try {
            return signupsById.get(id);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(SessionSignup signup) {
        writeLock.lock();
        try {
            signupsById.put(signup.getId(), signup);
        } finally {
            writeLock.unlock();
        }
    }
}

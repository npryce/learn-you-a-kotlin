package learnyouakotlin.part4;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemorySignupBook implements SignupBook {
    private final Map<SessionId, SignupSheet> signupsById = new HashMap<>();
    private final Lock readLock;
    private final Lock writeLock;

    {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    public @Nullable SignupSheet sheetFor(SessionId session) {
        readLock.lock();
        try {
            return signupsById.get(session);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(SignupSheet signup) {
        writeLock.lock();
        try {
            signupsById.put(signup.getSessionId(), signup);
        } finally {
            writeLock.unlock();
        }
    }
}

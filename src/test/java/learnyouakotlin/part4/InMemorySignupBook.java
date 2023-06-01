package learnyouakotlin.part4;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InMemorySignupBook implements SignupBook {
    private final Map<SessionId, SignupSheet> signupsById = new HashMap<>();

    @Override
    public @Nullable SignupSheet sheetFor(SessionId session) {
        return signupsById.get(session);
    }

    @Override
    public void save(SignupSheet signup) {
        signupsById.put(signup.getSessionId(), signup);
    }
}

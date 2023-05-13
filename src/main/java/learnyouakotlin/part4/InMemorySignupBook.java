package learnyouakotlin.part4;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class InMemorySignupBook implements SignupBook {
    private final Map<SessionId, SignupSheet> signupsById = new HashMap<>();

    @Override
    public @Nullable SignupSheet sheetFor(SessionId session) {
        return signupsById.get(session);
    }

    public void add(SignupSheet signup) {
        signupsById.put(signup.getSessionId(), signup);
    }
}

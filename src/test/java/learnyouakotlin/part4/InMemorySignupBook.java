package learnyouakotlin.part4;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class InMemorySignupBook implements SignupBook {
    private final Map<SessionId, SignupSheet> signupsById = new HashMap<>();

    @Override
    public @Nullable SignupSheet sheetFor(SessionId session) {
        SignupSheet stored = signupsById.get(session);
        if (stored == null) {
            return null;
        } else {
            // Return a copy of the sheet, to emulate behaviour of database
            SignupSheet loaded = new SignupSheet(stored.getSessionId(), stored.getCapacity(), stored.isSessionStarted());
            stored.getSignups().forEach(loaded::signUp);
            return loaded;
        }
    }

    @Override
    public void save(SignupSheet signup) {
        signupsById.put(signup.getSessionId(), signup);
    }
}

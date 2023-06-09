package example.signup;

import org.jetbrains.annotations.Nullable;

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
            SignupSheet loaded = SignupSheet.emptySignupSheet(stored.getSessionId(), stored.getCapacity());
            stored.getSignups().forEach(loaded::signUp);
            if (stored.isClosed()) {
                loaded.close();
            }
            return loaded;
        }
    }

    @Override
    public void save(SignupSheet signup) {
        signupsById.put(signup.getSessionId(), signup);
    }
}

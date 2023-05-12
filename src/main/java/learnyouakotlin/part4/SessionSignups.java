package learnyouakotlin.part4;

import javax.annotation.Nullable;

public interface SessionSignups {
    @Nullable SessionSignup load(SessionId id);

    void save(SessionSignup signup);
}

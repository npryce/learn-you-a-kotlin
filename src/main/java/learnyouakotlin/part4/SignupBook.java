package learnyouakotlin.part4;

import javax.annotation.Nullable;

public interface SignupBook {
    @Nullable SignupSheet sheetFor(SessionId session);
}

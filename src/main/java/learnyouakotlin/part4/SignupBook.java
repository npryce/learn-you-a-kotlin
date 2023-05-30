package learnyouakotlin.part4;


import org.jetbrains.annotations.Nullable;

public interface SignupBook {
    @Nullable SignupSheet sheetFor(SessionId session);

    void save(SignupSheet signup);
}

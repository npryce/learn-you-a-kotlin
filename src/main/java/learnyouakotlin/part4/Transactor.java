package learnyouakotlin.part4;

import java.io.IOException;

public interface Transactor<Resource> {
    interface Query<Resource,T> {
        T work(Resource resource) throws IOException;
    }

    interface Update<Resource> {
        void work(Resource resource) throws IOException;
    }

    <T> T perform(Query<Resource,T> work) throws IOException;

    void perform(Update<Resource> work) throws IOException;
}

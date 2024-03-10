package learnyouakotlin.part4;

import java.io.IOException;

public class InMemoryTransactor<Resource> implements Transactor<Resource> {
    private final Resource resource;

    public InMemoryTransactor(Resource resource) {
        this.resource = resource;
    }

    @Override
    public <T> T perform(Query<Resource, T> work) throws IOException {
        return work.work(resource);
    }

    @Override
    public void perform(Update<Resource> work) throws IOException {
        work.work(resource);
    }
}

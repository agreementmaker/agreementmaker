package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeTask;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BatchModeSpecImplTest {

    @Test(expected = UnsupportedOperationException.class)
    public void should_return_defensive_copy() {
        BatchModeSpecImpl spec = new BatchModeSpecImpl();
        spec.getTasks().add(new BatchModeTaskImpl());
    }

    @Test
    public void add_task_works() {
        BatchModeSpecImpl spec = new BatchModeSpecImpl();
        assertThat(spec.getTasks().size(), is(0));

        BatchModeTask task = new BatchModeTaskImpl();
        spec.addTask(task);
        assertThat(spec.getTasks().size(), is(1));
        assertThat(spec.getTasks().get(0), is(equalTo(task)));
    }
}
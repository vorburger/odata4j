package odata4j.backend;

import java.util.List;

public interface OEntity {

	public abstract List<OProperty<?>> getProperties();

	public abstract List<OProperty<?>> getKeyProperties();
}

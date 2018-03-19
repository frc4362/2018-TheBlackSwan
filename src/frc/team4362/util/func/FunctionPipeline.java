package frc.team4362.util.func;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class FunctionPipeline<T> {
	private Function<T, T> m_func;

	public FunctionPipeline(final List<Function<T, T>> functions) {
		m_func = functions.stream()
			.reduce(Function.identity(), Function::andThen);
	}

	@SafeVarargs
	public FunctionPipeline(final Function<T, T>... functions) {
		this(Arrays.asList(functions));
	}

	public FunctionPipeline() {
		this(Function.identity());
	}

	public FunctionPipeline<T> map(final Function<T, T> func) {
		m_func = m_func.andThen(func);
		return this;
	}

	public T apply(T input) {
		return m_func.apply(input);
	}
}

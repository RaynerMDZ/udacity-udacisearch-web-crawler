package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final Object target;
  private final ProfilingState state;
  private final ZonedDateTime startTime;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object target, ProfilingState state, ZonedDateTime startTime) {
    this.clock = Objects.requireNonNull(clock);
    this.target = Objects.requireNonNull(target);
    this.state = Objects.requireNonNull(state);
    this.startTime = Objects.requireNonNull(startTime);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.

    Object ans;
    Instant start = null;
    boolean profiled = method.getAnnotation(Profiled.class) != null;

    if (profiled) start = clock.instant();

    try {
      ans = method.invoke(this.target, args);

    } catch (InvocationTargetException | IllegalAccessException e) {
      throw e.getCause();

    } finally {
      if (profiled) {
        Duration duration = Duration.between(start, clock.instant());
        state.record(this.target.getClass(), method, duration);
      }
    }
    return ans;
  }

  @Override
    public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProfilingMethodInterceptor that = (ProfilingMethodInterceptor) o;

    return Objects.equals(clock, that.clock) &&
            Objects.equals(target, that.target) &&
            Objects.equals(state, that.state) &&
            Objects.equals(startTime, that.startTime);
  }
}

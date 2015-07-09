/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.ide.ui.tests.logical.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.compare.ide.ui.internal.logical.resolver.IComputation;
import org.eclipse.emf.compare.ide.ui.internal.logical.resolver.ResourceComputationScheduler;
import org.eclipse.emf.compare.ide.ui.internal.logical.resolver.ResourceComputationScheduler.ShutdownStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class ResourceComputationSchedulerTest {

	protected ResourceComputationScheduler<String> scheduler;

	protected boolean flag;

	@Test
	public void testInitializeCanBeCalledSeveralTimes() {
		scheduler.initialize();
		scheduler.initialize();
	}

	@Test
	public void testIsInitializedBeforeInit() {
		assertFalse(scheduler.isInitialized());
	}

	@Test
	public void testIsInitializedAfterInit() {
		scheduler.initialize();
		assertTrue(scheduler.isInitialized());
	}

	@Test
	public void testIsInitializedAfterDispose() {
		scheduler.initialize();
		scheduler.dispose();
		assertFalse(scheduler.isInitialized());
	}

	@Test
	public void testBasicExecution() throws Exception {
		scheduler.initialize();
		flag = false;
		Integer result = scheduler.call(new Callable<Integer>() {
			public Integer call() throws Exception {
				return Integer.valueOf(42);
			}
		}, new Runnable() {
			public void run() {
				flag = true;
			}
		});
		assertEquals(Integer.valueOf(42), result);
		// Flag will be true only if the post-treatment has been called...
		assertTrue(flag);
	}

	@Test(expected = OperationCanceledException.class)
	public void testInterruptedExceptionInCallCausesOperationCanceledException() throws Exception {
		scheduler.initialize();
		scheduler.call(new Callable<String>() {
			public String call() throws Exception {
				throw new InterruptedException();
			}
		}, null);
	}

	@Test(expected = OperationCanceledException.class)
	public void testOperationCanceledExceptionCall() throws Exception {
		scheduler.initialize();
		scheduler.call(new Callable<String>() {
			public String call() throws Exception {
				throw new OperationCanceledException();
			}
		}, null);
	}

	@Test
	public void testPostTreatmentIsCalledWhenExceptionInTreatment() throws Exception {
		scheduler.initialize();
		flag = false;
		try {
			scheduler.call(new Callable<Integer>() {
				public Integer call() throws Exception {
					throw new Exception("For test");
				}
			}, new Runnable() {
				public void run() {
					flag = true;
				}
			});
		} catch (RuntimeException e) {
			assertEquals("For test", e.getCause().getMessage());
		}
		// Flag will be true only if the post-treatment has been called...
		assertTrue(flag);
	}

	@Test
	public void testPostTreatmentCanBeNull() throws Exception {
		scheduler.initialize();
		Integer result = scheduler.call(new Callable<Integer>() {
			public Integer call() throws Exception {
				return Integer.valueOf(42);
			}
		}, null);
		assertEquals(Integer.valueOf(42), result);
	}

	@Test(expected = NullPointerException.class)
	public void testCallableCannotBeNull() throws Exception {
		scheduler.initialize();
		scheduler.call(null, new Runnable() {
			public void run() {
				// Nothing
			}
		});
	}

	@Test
	public void testComputedElements() {
		scheduler.initialize();
		assertTrue(scheduler.getComputedElements().isEmpty());
		scheduler.setComputedElements(Arrays.asList("a", "b", "c"));
		assertEquals(ImmutableSet.of("a", "b", "c"), scheduler.getComputedElements());
		scheduler.clearComputedElements();
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testComputeOneSuccess() throws Exception {
		scheduler.initialize();
		final CompStatus desc = new CompStatus();
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				scheduler.computeAll(Arrays.asList(new TestSuccessfulComputation(desc, "comp1")));
				assertEquals(ImmutableSet.of("comp1"), scheduler.getComputedElements());
				return Integer.valueOf(42);
			}
		}, null);
		checkSuccess(desc);
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testComputeSeveralSuccess() throws Exception {
		scheduler.initialize();
		final CompStatus[] statuses = new CompStatus[10];
		final List<TestSuccessfulComputation> computations = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			statuses[i] = new CompStatus();
			computations.add(new TestSuccessfulComputation(statuses[i], "comp" + i));
		}
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				for (int i = 0; i < 10; i++) {
					scheduler.computeAll(computations);
				}
				assertEquals(ImmutableSet.of("comp0", "comp1", "comp2", "comp3", "comp4", "comp5", "comp6",
						"comp7", "comp8", "comp9"), scheduler.getComputedElements());
				return Integer.valueOf(42);
			}
		}, null);
		for (int i = 0; i < 10; i++) {
			checkSuccess(statuses[i]);
		}
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testPostTreatmentOnFailureIsCalledOnOneFailedComputation() throws Exception {
		scheduler.initialize();
		final CompStatus cs = new CompStatus();
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				scheduler.computeAll(Arrays.asList(new TestFailedComputation(cs, "fail1")));
				assertEquals(ImmutableSet.of("fail1"), scheduler.getComputedElements());
				return Integer.valueOf(42);
			}
		}, null);
		checkFailure(cs);
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testPostTreatmentOnFailureIsCalledOnAllFailingComputations() throws Exception {
		scheduler.initialize();
		final CompStatus[] statuses = new CompStatus[10];
		final List<TestFailedComputation> computations = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			statuses[i] = new CompStatus();
			computations.add(new TestFailedComputation(statuses[i], "fail" + i));
		}
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				scheduler.computeAll(computations);
				assertEquals(ImmutableSet.of("fail0", "fail1", "fail2", "fail3", "fail4", "fail5", "fail6",
						"fail7", "fail8", "fail9"), scheduler.getComputedElements());
				return Integer.valueOf(42);
			}
		}, null);
		for (int i = 0; i < 10; i++) {
			checkFailure(statuses[i]);
		}
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testRunOneSuccess() throws Exception {
		scheduler.initialize();
		final CompStatus desc = new CompStatus();
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				scheduler.runAll(Arrays.asList(new UninterruptibleRunnable(desc)));
				assertTrue(scheduler.getComputedElements().isEmpty());
				return Integer.valueOf(42);
			}
		}, null);
		checkSuccess(desc);
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test
	public void testRunSeveralSuccess() throws Exception {
		scheduler.initialize();
		final CompStatus[] statuses = new CompStatus[10];
		final List<UninterruptibleRunnable> toBeRun = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			statuses[i] = new CompStatus();
			toBeRun.add(new UninterruptibleRunnable(statuses[i]));
		}
		Integer result = scheduler.call(new Callable<Integer>() {

			public Integer call() throws Exception {
				scheduler.runAll(toBeRun);
				assertTrue(scheduler.getComputedElements().isEmpty());
				return Integer.valueOf(42);
			}
		}, null);
		for (int i = 0; i < 10; i++) {
			checkSuccess(statuses[i]);
		}
		assertEquals(Integer.valueOf(42), result);
		assertTrue(scheduler.getComputedElements().isEmpty());
	}

	@Test(expected = NullPointerException.class)
	public void testScheduleComputationCannotRunOutsideCall() throws Exception {
		scheduler.initialize();
		final CompStatus desc = new CompStatus();
		scheduler.scheduleComputation(new TestSuccessfulComputation(desc, "comp"));
	}

	protected void checkSuccess(CompStatus state) {
		assertEquals(1, state.getCallCount());
		assertFalse(state.isInterrupted());
		if (!state.isSuccess() || state.isFailed()) {
			fail(state.getMessage());
		}
		assertEquals("as expected", state.getMessage());
	}

	protected void checkFailure(CompStatus state) {
		assertEquals(1, state.getCallCount());
		assertFalse(state.isInterrupted());
		if (state.isSuccess() || !state.isFailed()) {
			fail(state.getMessage());
		}
		assertEquals("as expected", state.getMessage());
	}

	protected void checkInterruptedAndSuccess(CompStatus state) {
		assertEquals(1, state.getCallCount());
		assertTrue(state.isInterrupted());
		if (!state.isSuccess() || state.isFailed()) {
			fail(state.getMessage());
		}
		assertEquals("as expected", state.getMessage());
	}

	protected void checkInterruptedAndFailure(CompStatus state) {
		assertEquals(1, state.getCallCount());
		assertTrue(state.isInterrupted());
		if (state.isSuccess() || !state.isFailed()) {
			fail(state.getMessage());
		}
		assertEquals("as expected", state.getMessage());
	}

	@Before
	public void setUp() {
		scheduler = new ResourceComputationScheduler<String>(10, TimeUnit.MILLISECONDS, null);
	}

	@After
	public void tearDown() {
		scheduler.dispose();
	}

	/**
	 * A test computation that is successful and updates it {@link CompStatus} accordingly when its
	 * post-treatment is called.
	 * 
	 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
	 */
	private final class TestSuccessfulComputation implements IComputation<String> {
		private final CompStatus cs;

		private final String name;

		private TestSuccessfulComputation(CompStatus cs, String name) {
			this.cs = cs;
			this.name = name;
		}

		public void run() {
			cs.addCall();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				cs.interrupt();
			}
		}

		public FutureCallback<Object> getPostTreatment() {
			return new FutureCallback<Object>() {
				public void onFailure(Throwable t) {
					cs.fail("onFailure() called on computation " + name + ", should have been onSuccess().");
				}

				public void onSuccess(Object r) {
					cs.success("as expected");
				}
			};
		}

		public String getKey() {
			return name;
		}
	}

	/**
	 * A test computation that systematically throws an exception when run, and updates its {@link CompStatus}
	 * accordingly if onFailure() is called on its post-treatment.
	 * 
	 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
	 */
	private final class TestFailedComputation implements IComputation<String> {
		private final CompStatus cs;

		private final String name;

		private TestFailedComputation(CompStatus desc, String name) {
			this.cs = desc;
			this.name = name;
		}

		public void run() {
			cs.addCall();
			throw new RuntimeException("Error for tests in computation " + name);
		}

		public FutureCallback<Object> getPostTreatment() {
			return new FutureCallback<Object>() {
				public void onFailure(Throwable t) {
					cs.fail("as expected");
				}

				public void onSuccess(Object r) {
					cs.success("onSuccess() called on computation " + name
							+ ", should have been onFailure().");
				}
			};
		}

		public String getKey() {
			return name;
		}
	}

	/**
	 * A test computation that is successful and updates it {@link CompStatus} accordingly when its
	 * post-treatment is called.
	 * 
	 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
	 */
	private final class UninterruptibleRunnable implements Runnable {
		private final CompStatus cs;

		private UninterruptibleRunnable(CompStatus desc) {
			this.cs = desc;
		}

		public void run() {
			cs.addCall();
			cs.success("as expected");
		}
	}

	/**
	 * Computation Status.
	 * 
	 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
	 */
	protected static class CompStatus {
		private boolean success = false;

		private boolean failed = false;

		private int callCount;

		private boolean interrupted;

		private ShutdownStatus shutdownStatus;

		private String message;

		public String getMessage() {
			return message;
		}

		public boolean isSuccess() {
			return success;
		}

		public synchronized void addCall() {
			callCount++;
		}

		public int getCallCount() {
			return callCount;
		}

		public void success(String msg) {
			this.success = true;
			this.failed = false;
			message = msg;
		}

		public boolean isFailed() {
			return failed;
		}

		public void fail(String msg) {
			this.failed = true;
			this.success = false;
			message = msg;
		}

		public void interrupt() {
			interrupted = true;
		}

		public boolean isInterrupted() {
			return interrupted;
		}

		public void setShutdownStatus(ShutdownStatus shutdownStatus) {
			this.shutdownStatus = shutdownStatus;
		}

		public ShutdownStatus getShutdownStatus() {
			return shutdownStatus;
		}
	}
}

package com.ledoyen.aash.junit.runner;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;

import com.ledoyen.aash.tool.Collections2;
import com.ledoyen.aash.tool.Either;
import com.ledoyen.aash.tool.EitherLeftMapper;
import com.ledoyen.aash.tool.EitherRightMapper;

public class AashJUnitRunner extends ParentRunner<Either<Runner, FrameworkMethod>> {

	private Either<AashJUnitSuiteExposer, AashJUnitBlockClassRunnerExposer> delegate;

	public AashJUnitRunner(Class<?> klass) throws Throwable {
		super(klass);
		
		// TODO analyze given plugins the use of one or another
		delegate = Either.Right(new AashJUnitBlockClassRunnerExposer(klass));
	}

	@Override
	protected List<Either<Runner, FrameworkMethod>> getChildren() {
		if(delegate.isLeft()) {
			return Collections2.map(delegate.left().getC(), new EitherLeftMapper<Runner, FrameworkMethod>());
		} else {
			return Collections2.map(delegate.right().getC(), new EitherRightMapper<Runner, FrameworkMethod>());
		}
	}

	@Override
	protected Description describeChild(Either<Runner, FrameworkMethod> child) {
		if(delegate.isLeft()) {
			return delegate.left().describeC(child.left());
		} else {
			return delegate.right().describeC(child.right());
		}
	}

	@Override
	protected void runChild(Either<Runner, FrameworkMethod> child, RunNotifier notifier) {
		if(delegate.isLeft()) {
			delegate.left().runC(child.left(), notifier);
		} else {
			delegate.right().runC(child.right(), notifier);
		}
	}
}

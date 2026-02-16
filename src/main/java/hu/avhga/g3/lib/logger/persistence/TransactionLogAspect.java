package hu.avhga.g3.lib.logger.persistence;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Aspect
@Component
public class TransactionLogAspect implements TransactionSynchronization, Ordered {
	private static final Logger transactionLogger = LoggerFactory.getLogger(TransactionLogAspect.class);

	private final ThreadLocal<Integer> level = new ThreadLocal<>();
	private final ThreadLocal<String> uid = new ThreadLocal<>();
	private final ThreadLocal<Long> trStart = new ThreadLocal<>();

	@Around("@annotation(org.springframework.transaction.annotation.Transactional)")
	public Object logEndpoints(ProceedingJoinPoint pjp) throws Throwable {
		String method = pjp.getSignature().toString();
		init();

		Integer lvl = increaseLevel();

		transactionLogger.trace("{} in transaction: {}", method, lvl);
		if ( lvl == 1 ) {
			transactionLogger.info("Új tranzakció indult: {}", uid.get());
			boolean registered = false;
			List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
			for ( TransactionSynchronization s : synchronizations ) {
				if ( s instanceof TransactionLogAspect ) {
					registered = true;
					break;
				}
			}
			if ( !registered ) {
				transactionLogger.debug("registering synchronization");
				TransactionSynchronizationManager.registerSynchronization(this);
			}
		}
		try {
			return pjp.proceed();
		} finally {
			lvl = decreaseLevel();
			transactionLogger.trace("{} invocation finished: {}", method, lvl);
		}
	}

	private void init() {
		if ( trStart.get() == null ) {
			trStart.set(System.currentTimeMillis());
		}
		if ( uid.get() == null ) {
			uid.set(TransactionSynchronizationManager.getCurrentTransactionName() + "@" + System.nanoTime());
		}
		if ( level.get() == null ) {
			level.set(0);
		}
	}

	private void cleanup() {
		trStart.remove();
		uid.remove();
	}

	private Integer increaseLevel() {
		Integer lvl = level.get();
		if ( TransactionSynchronizationManager.isActualTransactionActive() ) {
			level.set(++lvl);
		} else {
			transactionLogger.debug("@Transaction annotacio van, de nincs tranzakcioban a hivas");
		}
		return lvl;
	}

	private Integer decreaseLevel() {
		Integer lvl = level.get();
		if ( TransactionSynchronizationManager.isActualTransactionActive() ) {
			level.set(--lvl);
		}
		return lvl;
	}

	@Override
	public void afterCompletion(int status) {
		if ( status == STATUS_COMMITTED ) {
			transactionLogger.info("Tranzakció sikeresen lezárult: {}", uid.get());
		} else if ( status == STATUS_ROLLED_BACK ) {
			transactionLogger.info("Tranzakcióban hiba történt: {}", uid.get());
		} else {
			transactionLogger.info("Tranzakció ismeretlen eredménnyel zárult: {}", uid.get());
		}
		if ( transactionLogger.isDebugEnabled() && trStart.get() != null ) {
			long dura = System.currentTimeMillis() - trStart.get();
			String time = String.format("%d:%02d:%02d.%03d",
					dura / 3600000,
					(dura / 1000 % 3600) / 60,
					dura / 1000 % 60,
					dura % 1000);
			transactionLogger.debug("Tranzakció hossza: {}", time);
		}
		cleanup();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void afterCommit() {
		// nem kell
	}

	@Override
	public void beforeCommit(boolean arg0) {
		// nem kell
	}

	@Override
	public void beforeCompletion() {
		// nem kell
	}

	@Override
	public void flush() {
		// nem kell
	}

	@Override
	public void resume() {
		// nem kell
	}

	@Override
	public void suspend() {
		// nem kell
	}
}

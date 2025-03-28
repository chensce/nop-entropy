/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.dao.txn.impl;

import io.nop.api.core.annotations.txn.TransactionPropagation;
import io.nop.api.core.exceptions.NopException;
import io.nop.api.core.util.FutureHelper;
import io.nop.commons.util.IoHelper;
import io.nop.dao.dialect.IDialect;
import io.nop.dao.txn.ITransaction;
import io.nop.dao.txn.ITransactionManager;
import io.nop.dao.txn.ITransactionTemplate;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.nop.api.core.context.ContextProvider.completeAsyncOnContext;
import static io.nop.dao.DaoErrors.ARG_TXN;
import static io.nop.dao.DaoErrors.ARG_TXN_GROUP;
import static io.nop.dao.DaoErrors.ERR_TXN_NOT_ALLOW_TRANSACTION;
import static io.nop.dao.DaoErrors.ERR_TXN_NOT_IN_TRANSACTION;
import static io.nop.dao.DaoErrors.ERR_TXN_NOT_REGISTERED;
import static io.nop.dao.utils.DaoHelper.isDefaultQuerySpace;

public class TransactionTemplateImpl implements ITransactionTemplate {

    private ITransactionManager transactionManager;

    public ITransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Inject
    public void setTransactionManager(ITransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public IDialect getDialectForQuerySpace(String querySpace) {
        return transactionManager.getDialectForQuerySpace(querySpace);
    }

    @Override
    public Connection openConnection(String querySpace) {
        return transactionManager.openConnection(querySpace);
    }

    @Override
    public boolean isQuerySpaceDefined(String querySpace) {
        return transactionManager.isQuerySpaceDefined(querySpace);
    }

    @Override
    public String getMainTxnGroup(String txnGroup) {
        if (isDefaultQuerySpace(txnGroup))
            return null;
        return transactionManager.getMainTxnGroup(txnGroup);
    }

    @Override
    public ITransaction getRegisteredTransaction(String txnGroup) {
        ITransaction txn = transactionManager.getRegisteredTransaction(txnGroup);
        if (txn == null) {
            ITransaction groupTxn = getMainGroupTransaction(txnGroup);
            if (groupTxn != null)
                return groupTxn.getSubTransaction(txnGroup);

        }
        return txn;
    }

    private ITransaction getMainGroupTransaction(String txnGroup) {
        String mainGroup = getMainTxnGroup(txnGroup);
        if (mainGroup != null) {
            ITransaction groupTxn = transactionManager.getRegisteredTransaction(mainGroup);
            return groupTxn;
        }
        return null;
    }

    static class TxnState {
        String txnGroup;
        ITransaction txn;

        boolean newlyOpen;
        boolean newlyCreated;
        boolean groupNewlyCreated;

        String mainGroup;
        ITransaction groupTxn;

        ITransaction prevTxn;

        TxnState newState() {
            TxnState state = new TxnState();
            state.txnGroup = txnGroup;
            state.mainGroup = mainGroup;
            state.prevTxn = getRealTxn();
            return state;
        }

        ITransaction getRealTxn() {
            if (mainGroup != null)
                return groupTxn;
            return txn;
        }

        boolean isOpened() {
            if (mainGroup != null) {
                return groupTxn != null && groupTxn.isTransactionOpened();
            } else {
                return txn != null && txn.isTransactionOpened();
            }
        }
    }

    private TxnState getTxnState(String txnGroup, boolean allowCreate) {
        TxnState state = new TxnState();
        state.txnGroup = txnGroup;
        String mainGroup = getMainTxnGroup(txnGroup);
        if (mainGroup != null && !mainGroup.equals(txnGroup)) {
            state.mainGroup = mainGroup;
            ITransaction groupTxn = transactionManager.getRegisteredTransaction(mainGroup);
            ITransaction txn;
            if (groupTxn != null) {
                txn = groupTxn.getSubTransaction(txnGroup);
                if (txn == null && allowCreate) {
                    // 如果主事务已经打开，则需要自动打开子事务
                    txn = transactionManager.newTransaction(txnGroup);
                    groupTxn.addSubTransaction(txn);
                    if (groupTxn.isTransactionOpened()) {
                        txn.open();
                    }
                    // 这里事务的状态由主事务决定，所以不用标记newlyOpen
                    state.newlyCreated = true;
                }
            } else {
                txn = transactionManager.getRegisteredTransaction(txnGroup);
                if (txn == null && allowCreate) {
                    // 只是新建事务对象，但是并没有调用open打开事务
                    txn = transactionManager.newTransaction(txnGroup);
                    state.newlyCreated = true;
                    state.prevTxn = transactionManager.registerTransaction(txn);
                }
            }
            state.txn = txn;
            state.groupTxn = groupTxn;
        } else {
            ITransaction txn = transactionManager.getRegisteredTransaction(txnGroup);
            if (txn == null && allowCreate) {
                // 只是新建事务对象，但是并没有调用open打开事务
                txn = transactionManager.newTransaction(txnGroup);
                state.newlyCreated = true;
                state.prevTxn = transactionManager.registerTransaction(txn);
            }
            state.txn = txn;
        }
        return state;
    }

    @Override
    public <T> T runWithoutTransaction(String txnGroup, Supplier<T> task) {
        ITransaction txn = getRegisteredTransaction(txnGroup);
        if (txn == null) {
            return task.get();
        } else {
            transactionManager.unregisterTransaction(txn);
            try {
                return task.get();
            } finally {
                transactionManager.registerTransaction(txn);
            }
        }
    }

    @Override
    public <T> CompletionStage<T> runInTransactionAsync(String txnGroup, TransactionPropagation propagation,
                                                        Function<ITransaction, CompletionStage<T>> task) {
        TxnState state = openTransaction(txnGroup, propagation);

        CompletionStage<T> future;
        try {
            future = task.apply(state.txn);
        } catch (Throwable e) {
            future = FutureHelper.reject(e);
        }
        return completeAsyncOnContext(future, (ret, err) -> {
            if (NopException.shouldRollback(err)) {
                // 出错时总是执行rollback，确保数据库资源得到释放
                return rollbackTransactionAsync(state, err).whenComplete((a, b) -> {
                    throw NopException.adapt(err);
                }).thenApply(v -> null);
            } else {
                return commitTransactionAsync(state).exceptionally(err2 -> {
                    rollbackTransaction(state, err2);
                    throw NopException.adapt(err2);
                }).thenApply(v -> FutureHelper.returnResult(ret, err));
            }
        }).whenComplete((ret, err) -> {
            cleanupTransaction(state);
        });
    }

    @Override
    public <T> T runInTransaction(String txnGroup, TransactionPropagation propagation, Function<ITransaction, T> task) {
        TxnState state = openTransaction(txnGroup, propagation);

        boolean executed = false;
        try {
            T result = task.apply(state.txn);
            executed = true;
            commitTransaction(state);
            return result;
        } catch (Exception e) {
            if (executed || NopException.shouldRollback(e)) {
                rollbackTransaction(state, e);
            } else {
                try {
                    commitTransaction(state);
                } catch (Exception e2) {
                    rollbackTransaction(state, e2);
                }
            }
            throw NopException.adapt(e);
        } finally {
            cleanupTransaction(state);
        }
    }

    private TxnState openTransaction(String txnGroup, TransactionPropagation propagation) {
        if (propagation == null)
            propagation = TransactionPropagation.REQUIRED;
        TxnState state;
        switch (propagation) {
            case REQUIRED: {
                state = getTxnState(txnGroup, true);
                openExistingTransaction(state);
                break;
            }
            case SUPPORTS: {
                state = getTxnState(txnGroup, true);
                break;
            }
            case MANDATORY: {
                state = getTxnState(txnGroup, true);
                if (!state.isOpened())
                    throw new NopException(ERR_TXN_NOT_IN_TRANSACTION).param(ARG_TXN_GROUP, txnGroup);
                break;
            }
            case REQUIRES_NEW: {
                state = getTxnState(txnGroup, false);
                state = state.newState();
                createNewTransaction(state);
                break;
            }
            case NOT_SUPPORTED: {
                state = getTxnState(txnGroup, false);
                state = state.newState();
                if (state.prevTxn != null) {
                    if (!transactionManager.unregisterTransaction(state.prevTxn))
                        throw new NopException(ERR_TXN_NOT_REGISTERED).param(ARG_TXN, state.prevTxn);
                }
                break;
            }
            case NEVER: {
                state = getTxnState(txnGroup, false);
                if (state.isOpened())
                    throw new NopException(ERR_TXN_NOT_ALLOW_TRANSACTION).param(ARG_TXN_GROUP, txnGroup);
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }
        return state;
    }

    private void openExistingTransaction(TxnState state) {
        if (state.groupTxn != null) {
            // 如果存在主事务，则事务的打开关闭完全交由主事务负责
            if (!state.groupTxn.isTransactionOpened()) {
                state.groupTxn.open();
                state.newlyOpen = true;
            }
        } else if (!state.txn.isTransactionOpened()) {
            state.txn.open();
            state.newlyOpen = true;
        }
    }

    private void createNewTransaction(TxnState state) {
        if (state.mainGroup != null) {
            ITransaction groupTxn = transactionManager.newTransaction(state.mainGroup);
            ITransaction txn = null;
            try {
                txn = transactionManager.newTransaction(state.txnGroup);
                groupTxn.addSubTransaction(txn);
                groupTxn.open();
            } catch (Exception e) {
                groupTxn.close();
                IoHelper.safeCloseObject(txn);
                throw NopException.adapt(e);
            }
            transactionManager.registerTransaction(groupTxn);
            state.groupTxn = groupTxn;
            state.txn = txn;
            state.groupNewlyCreated = true;
            state.newlyCreated = true;
            state.newlyOpen = true;
        } else {
            ITransaction txn = transactionManager.newTransaction(state.txnGroup);
            try {
                txn.open();
            } catch (Exception e) {
                IoHelper.safeCloseObject(txn);
                throw NopException.adapt(e);
            }
            transactionManager.registerTransaction(txn);
            state.txn = txn;
            state.newlyCreated = true;
            state.newlyOpen = true;
        }
    }

    private CompletionStage<Void> commitTransactionAsync(TxnState state) {
        if (state.newlyOpen) {
            if (state.groupTxn != null) {
                return state.groupTxn.commitAsync();
            } else if (state.txn != null) {
                return state.txn.commitAsync();
            }
        }
        return FutureHelper.success(null);
    }

    private CompletionStage<Void> rollbackTransactionAsync(TxnState state, Throwable e) {
        if (state.groupTxn != null) {
            if (state.newlyCreated && state.groupTxn.isTransactionOpened())
                return state.groupTxn.rollbackAsync(e);
        } else if (state.txn != null) {
            if (state.newlyCreated && state.txn.isTransactionOpened())
                return state.txn.rollbackAsync(e);
        }
        return FutureHelper.success(null);
    }

    private void commitTransaction(TxnState state) {
        // 负责打开事务的函数负责提交
        if (state.newlyOpen) {
            if (state.groupTxn != null) {
                state.groupTxn.commit();
            } else if (state.txn != null) {
                state.txn.commit();
            }
        }
    }

    private void rollbackTransaction(TxnState state, Throwable e) {
        // 打开事务的函数负责回滚。
        if (state.groupTxn != null) {
            // 事务未打开的情况下不允许回滚
            if (state.groupNewlyCreated && state.groupTxn.isTransactionOpened())
                state.groupTxn.rollback(e);
        } else if (state.txn != null) {
            if (state.newlyCreated && state.txn.isTransactionOpened())
                state.txn.rollback(e);
        }
    }

    private void cleanupTransaction(TxnState state) {
        RuntimeException ex = null;
        // 新建的事务需要取消注册
        if (state.groupNewlyCreated) {
            if (!transactionManager.unregisterTransaction(state.groupTxn)) {
                ex = new NopException(ERR_TXN_NOT_REGISTERED).param(ARG_TXN, state.groupTxn);
            }
        } else if (state.newlyCreated && state.groupTxn == null) {
            // 如果存在主事务，则实际txn是注册到groupTxn中，而不是注册为全局的事务，因此这里也就不需要取消注册
            if (!transactionManager.unregisterTransaction(state.txn)) {
                ex = new NopException(ERR_TXN_NOT_REGISTERED).param(ARG_TXN, state.groupTxn);
            }
        }

        // REQUIRE_NEW需要恢复此前的事务
        if (state.prevTxn != null) {
            transactionManager.registerTransaction(state.prevTxn);
        }

        // 新打开的主事务需要关闭。如果是分支事务，则跟随主事务一起关闭
        // newlyCreated的事务即使没有open，回调函数中也有可能调用getConnection导致实际上创建了非事务性的连接
        if (state.groupTxn != null) {
            if (state.newlyOpen || state.groupNewlyCreated) {
                state.groupTxn.close();
            }
        } else if (state.txn != null) {
            if (state.newlyOpen || state.newlyCreated)
                state.txn.close();
        }

        if (ex != null)
            throw ex;
    }
}
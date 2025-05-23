/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.batch.core;

import io.nop.core.context.IExecutionContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 批处理的一个执行单元。例如100条记录组成一个chunk，一个chunk全部执行完毕之后才提交一次，而不是每处理一条记录就提交一次事务。
 */
public interface IBatchChunkContext extends IExecutionContext {
    <T> List<T> getChunkItems();

    <T> void setChunkItems(List<T> items);

    /**
     * 当多线程执行时，这里对应线程的顺序编号，范围在[0,concurrency)之内。可以用于内部数据分区时的依据
     */
    int getThreadIndex();

    void setThreadIndex(int threadIndex);

    int getConcurrency();

    void setConcurrency(int concurrency);

    IBatchTaskContext getTaskContext();

    <T> Set<T> getCompletedItems();

    default int getCompletedItemCount() {
        Set<?> items = getCompletedItems();
        return items == null ? 0 : items.size();
    }

    <T> void addCompletedItem(T item);

    <T> void addCompletedItems(Collection<T> items);

    int getProcessCount();

    void setProcessCount(int count);

    default void incProcessCount() {
        setProcessCount(getProcessCount() + 1);
    }

    /**
     * 第一次执行时retryCount=0。重试执行时retryCount从1开始，不断递增
     */
    int getRetryCount();

    void setRetryCount(int retryCount);

    default void incRetryCount() {
        setRetryCount(getRetryCount() + 1);
    }

    int getLoadRetryCount();

    void setLoadRetryCount(int loadRetryCount);

    default void incLoadRetryCount() {
        setLoadRetryCount(getLoadRetryCount() + 1);
    }

    default boolean isLoadRetrying() {
        return getLoadRetryCount() > 0;
    }

    /**
     * 是否单条执行。如果是单条执行，则不会因为其他条目的失败导致回滚
     */
    boolean isSingleMode();

    void setSingleMode(boolean singleMode);

    /**
     * 是否是重试执行。第一次执行时retrying=false，如果chunk的第一次执行失败，逐条重试的时候retrying为true。
     */
    default boolean isRetrying() {
        return getRetryCount() > 0;
    }

    default boolean isSingleItem() {
        return getChunkItems().size() == 1;
    }

    void initChunkLatch(CountDownLatch latch);

    CountDownLatch getChunkLatch();

    void countDown();

    long getRowNumber(Object item);

    void setRowNumber(Object item, long rowNumber);
}
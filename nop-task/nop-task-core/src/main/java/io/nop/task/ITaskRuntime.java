/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.task;

import io.nop.api.core.context.ContextProvider;
import io.nop.api.core.context.IContext;
import io.nop.api.core.util.ICancellable;
import io.nop.commons.concurrent.semaphore.ISemaphore;
import io.nop.commons.concurrent.executor.IScheduledExecutor;
import io.nop.commons.concurrent.executor.IThreadPoolExecutor;
import io.nop.commons.concurrent.ratelimit.IRateLimiter;
import io.nop.commons.lang.IEditableTagSetSupport;
import io.nop.commons.util.StringHelper;
import io.nop.core.context.IEvalContext;
import io.nop.core.context.IServiceContext;
import io.nop.task.metrics.ITaskFlowMetrics;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * <p>1. Task表达单次请求处理过程。request保存请求对象，而response保存结果对象。</p>
 * <p>2. 一个Task包含多个TaskStep。Task可能发起子Task。</p>
 * <p>3. 多个TaskInstance属于同一个jobInstance。</p>
 * <p>
 * 4. TaskContext为多个步骤并发运行时所共享的上下文对象。
 * <p>
 * 5. attributes保存不需要持久化的临时变量，taskVars保存需要持久化的Task级别的状态变量。
 */
public interface ITaskRuntime extends IEvalContext, ICancellable, IEditableTagSetSupport {
    IServiceContext getSvcCtx();

    IContext getContext();

    default String newStepInstanceId() {
        return StringHelper.generateUUID();
    }

    default String getLocale() {
        IServiceContext ctx = getSvcCtx();
        if (ctx != null) {
            return ctx.getContext().getLocale();
        }
        return ContextProvider.currentLocale();
    }

    ITaskState getTaskState();

    ITaskFlowMetrics getMetrics();

    /**
     * 分配一个新的runId
     */
    int newRunId();

    default String getTaskName() {
        return getTaskState().getTaskName();
    }

    default long getTaskVersion() {
        return getTaskState().getTaskVersion();
    }

    default String getTaskInstanceId() {
        return getTaskState().getTaskInstanceId();
    }

    default String getJobInstanceId() {
        return getTaskState().getJobInstanceId();
    }

    default String getTaskNameAndVersion() {
        return getTaskName() + '/' + getTaskVersion();
    }

    String getTaskDescription();

    void setTaskDescription(String description);

    ITaskRuntime newChildRuntime(ITask task, boolean saveState);

    ITaskFlowManager getTaskManager();

    default Object getTaskVar(String name) {
        Map<String, Object> vars = getTaskVars();
        return vars == null ? null : vars.get(name);
    }

    default boolean hasTaskVar(String name) {
        Map<String, Object> vars = getTaskVars();
        return vars != null && vars.containsKey(name);
    }

    default void setTaskVar(String name, Object value) {
        getTaskState().setTaskVar(name, value);
    }

    default Map<String, Object> getTaskVars() {
        return getTaskState().getTaskVars();
    }


    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    Set<String> getAttributeKeys();

    default Object getInput(String name) {
        return getEvalScope().getValue(name);
    }

    default void setInput(String name, Object value) {
        getEvalScope().setLocalValue(name, value);
    }

    default void setInputs(Map<String, Object> inputs) {
        if (inputs != null)
            inputs.forEach(this::setInput);
    }

    Object computeAttributeIfAbsent(String name, Function<String, Object> action);

    IScheduledExecutor getScheduledExecutor();

    IThreadPoolExecutor getThreadPoolExecutor(String executorBean);

    ITaskStepRuntime newMainStepRuntime();

    IRateLimiter getRateLimiter(String key, double requestsPerSecond, boolean global);

    ISemaphore getSemaphore(String key, int maxPermits, boolean global);

    /**
     * 任务执行完毕之后清理资源
     *
     * @param cleanup
     */
    void addTaskCleanup(Runnable cleanup);

    void runCleanup();
}
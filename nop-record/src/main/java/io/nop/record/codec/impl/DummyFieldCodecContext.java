package io.nop.record.codec.impl;

import io.nop.core.lang.eval.DisabledEvalScope;
import io.nop.core.lang.eval.IEvalScope;
import io.nop.record.codec.IFieldCodecContext;
import io.nop.record.model.RecordTypeMeta;

public class DummyFieldCodecContext implements IFieldCodecContext {
    public static final DummyFieldCodecContext INSTANCE = new DummyFieldCodecContext();

    @Override
    public IEvalScope getEvalScope() {
        return DisabledEvalScope.INSTANCE;
    }

    @Override
    public String getFieldPath() {
        return null;
    }

    @Override
    public void enterField(String name) {

    }

    @Override
    public void leaveField(String name) {

    }

    @Override
    public RecordTypeMeta getType(String name) {
        return null;
    }
}
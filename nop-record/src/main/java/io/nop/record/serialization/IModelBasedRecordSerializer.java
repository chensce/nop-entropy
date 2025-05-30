package io.nop.record.serialization;

import io.nop.record.codec.IFieldCodecContext;
import io.nop.record.model.RecordFieldMeta;
import io.nop.record.model.RecordObjectMeta;
import io.nop.record.writer.IDataWriterBase;

import java.io.IOException;

public interface IModelBasedRecordSerializer<Output extends IDataWriterBase> {
    boolean writeObject(Output out, RecordObjectMeta recordMeta, Object record, IFieldCodecContext context) throws IOException;

    boolean writeField(Output out, RecordFieldMeta field, Object record, IFieldCodecContext context) throws IOException;
}

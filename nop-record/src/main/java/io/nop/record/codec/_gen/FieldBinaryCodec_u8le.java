
package io.nop.record.codec._gen;

import io.nop.record.codec.IFieldBinaryEncoder;
import io.nop.record.codec.IFieldCodecContext;
import io.nop.record.reader.IBinaryDataReader;
import io.nop.record.writer.IBinaryDataWriter;
import io.nop.record.codec.impl.StaticFieldBinaryCodecFactory;

import java.io.IOException;

public class FieldBinaryCodec_u8le extends StaticFieldBinaryCodecFactory{
    public static final FieldBinaryCodec_u8le INSTANCE = new FieldBinaryCodec_u8le();

    @Override
    public Object decode(IBinaryDataReader input, Object record, int length,
                        IFieldCodecContext context) throws IOException{
        return input.readU8le();
    }

    @Override
    public void encode(IBinaryDataWriter output, Object value, int length,
        IFieldCodecContext context, IFieldBinaryEncoder bodyEncoder) throws IOException{
        if(value == null){
            output.writeU8le(0L);
        }else{
            
                    output.writeU8le(((Number)value).longValue());
                

        }
    }
}
